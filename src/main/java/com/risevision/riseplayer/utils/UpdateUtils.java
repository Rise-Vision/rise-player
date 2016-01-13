package com.risevision.riseplayer.utils;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import com.risevision.riseplayer.Config;
import com.risevision.riseplayer.Log;
import com.risevision.riseplayer.externallogger.ExternalLogger;
import com.risevision.riseplayer.externallogger.InsertSchema;

public class UpdateUtils {
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    private static final String STABLE_CHANNEL = "Stable";
    private static final String LATEST_CHANNEL = "Latest";
    private static final String FORCE_STABLE = "ForceStable";
    private static final String UPDATE_ATTEMPT_PROPERTY = "attempt";
    private static final String LATEST_CHECK_PROPERTY = "latestCheck";
    private static final Integer MAX_UPDATE_ATTEMPTS = 3;
    private static final Integer MINUTES_BETWEEN_UPDATE_ATTEMPTS = 30;

    public void restartIfUpdateAvailable() {
        Properties remoteComponents = getRemoteComponentsVersions();
        boolean includeBrowser = isBrowserUpgradeable();
        boolean updatesOnStable = componentUpdatesAvailable(remoteComponents, STABLE_CHANNEL, includeBrowser);
        boolean updatesOnLatest = componentUpdatesAvailable(remoteComponents, LATEST_CHANNEL, includeBrowser);
        boolean forceStable = remoteComponents.getProperty(FORCE_STABLE).equals("true");
        // Restart the player only if forceStable was requested and local does not match Stable, or if neither Stable or Latest match local
        boolean updatesAvailable = forceStable ? updatesOnStable : (updatesOnStable && updatesOnLatest);

        if (updatesAvailable) {
            if (componentsReachable(remoteComponents, STABLE_CHANNEL) && componentsReachable(remoteComponents, LATEST_CHANNEL)) {
                Properties cfg = getComponentsUpdateAttempts();
                int attempts = 0;
                Date lastCheck = parseDate("01/01/1990 00:00:00");

                if (cfg != null) {
                    if (isInteger(cfg.getProperty(UPDATE_ATTEMPT_PROPERTY))) {
                        attempts = new Integer(cfg.getProperty(UPDATE_ATTEMPT_PROPERTY));
                    }

                    if (parseDate(cfg.getProperty(LATEST_CHECK_PROPERTY)) != null) {
                        lastCheck = parseDate(cfg.getProperty(LATEST_CHECK_PROPERTY));
                    }
                }

                if (attempts >= MAX_UPDATE_ATTEMPTS && lastCheck.getTime() + 24 * 60 * 60 * 1000 < new Date().getTime()) {
                    Log.info("Resetting update attempts after 24hs since the last failed attempt");
                    ExternalLogger.logExternal(InsertSchema.withEvent("update attempts reset"));

                    saveComponentsUpdateAttempts(1);
                    Utils.runAutoUpdateScript();
                } else if (attempts < MAX_UPDATE_ATTEMPTS) {
                    if (lastCheck.getTime() + (1 + Math.random()) * MINUTES_BETWEEN_UPDATE_ATTEMPTS * 60 * 1000 < new Date().getTime()) {
                        Log.info("Attempting regular update #" + (attempts + 1));
                        ExternalLogger.logExternal(InsertSchema.withEvent("update attempt", "#" + (attempts + 1)));

                        saveComponentsUpdateAttempts(attempts + 1);
                        Utils.runAutoUpdateScript();
                    } else {
                        Log.warn("Update attempt timeout not reached. Not restarting");
                    }
                } else {
                    Log.warn("Maximum number of update attempts reached. Not restarting");
                    ExternalLogger.logExternal(InsertSchema.withEvent("update attempts max reached"));
                }
            } else {
                Log.warn("Could not access some of the remote components. Not restarting");
                ExternalLogger.logExternal(InsertSchema.withEvent("components list fetch failed"));
            }
        } else {
            Log.info("Installed components match remote versions. Not restarting");
            saveComponentsUpdateAttempts(0);
        }
    }

    protected boolean componentUpdatesAvailable(Properties remoteComponents, String channel, boolean includeBrowser) {
        Map<String, String> componentNames = new HashMap<String, String>();
        componentNames.put("InstallerVersion", "installer");
        componentNames.put("CacheVersion" + channel, "RiseCache");
        componentNames.put("JavaVersion" + channel, "java");
        componentNames.put("PlayerVersion" + channel, "RisePlayer");

        if (includeBrowser) {
            componentNames.put("BrowserVersion" + channel, "chromium");
        }

        boolean versionsMatch = true;

        for (String remoteName : componentNames.keySet()) {
            String remoteVersion = remoteComponents.getProperty(remoteName);
            String installedName = componentNames.get(remoteName);
            String installedVersion = getInstalledComponentVersion(installedName);

            if (remoteVersion == null ||
                    installedVersion == null ||
                    !remoteVersion.equals(installedVersion)) {
                versionsMatch = false;
                break;
            }
        }

        return !versionsMatch;
    }

    protected boolean componentsReachable(Properties remoteComponents, String channel) {
        String remoteNames[] = new String[]{"InstallerURL", "BrowserURL" + channel, "CacheURL" + channel, "JavaURL" + channel, "PlayerURL" + channel};

        for (int i = 0; i < remoteNames.length; i++) {
            String url = remoteComponents.getProperty(remoteNames[i]);

            if (!isResourceReachable(url)) {
                return false;
            }
        }

        return true;
    }

    protected Properties getRemoteComponentsVersions() {
        try {
            String prefixUrl = "http://install-versions.risevision.com/remote-components-";
            String lnxVersion = System.getProperty("os.arch").equals("x86") ? "lnx-32" : "lnx-64";
            String suffixUrl = !Config.playerOS.equals("win") ? lnxVersion : "win";

            URL resource = new URL(prefixUrl + suffixUrl + ".cfg");

            return loadPropertiesFromStream(resource.openStream());
        } catch (Exception e) {
            Log.error("Error loading remote components versions");
            throw new RuntimeException(e);
        }
    }

    protected String getInstalledComponentVersion(String name) {
        File versionFile = new File(Config.appPath, name + ".ver");
        String version = "notfound";

        try {
            if (versionFile.exists()) {
                InputStreamReader is = new InputStreamReader(new FileInputStream(versionFile));
                BufferedReader reader = new BufferedReader(is);

                version = reader.readLine();
                is.close();

                return version;
            } else {
                Log.warn("Config file " + name + ".ver does not exist");
            }
        } catch (Exception e) {
            Log.error("Error loading config file " + name + " " + e.getMessage());
            throw new RuntimeException(e);
        }

        return version;
    }

    protected Properties getComponentsUpdateAttempts() {
        File f = new File(Config.appPath, Config.updateAttemptsFile);

        try {
            if (f.exists()) {
                return loadPropertiesFromStream(new BufferedInputStream(new FileInputStream(f)));
            } else {
                Log.info("Update attempts config file does not exist");

                return null;
            }
        } catch (Exception e) {
            Log.error("Error loading update attempts config file");
            throw new RuntimeException(e);
        }
    }

    protected void saveComponentsUpdateAttempts(int attempts) {
        File f = new File(Config.appPath, Config.updateAttemptsFile);

        try {
            FileOutputStream fos = new FileOutputStream(f);
            Properties props = new Properties();

            props.setProperty(UPDATE_ATTEMPT_PROPERTY, String.valueOf(attempts));
            props.setProperty(LATEST_CHECK_PROPERTY, DATE_FORMAT.format(new Date()));

            props.store(fos, "Upgrade attempts information");
            fos.flush();
            fos.close();
        } catch (Exception e) {

        }
    }

    protected boolean isResourceReachable(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

            con.setInstanceFollowRedirects(false);
            con.setRequestMethod("HEAD");

            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            Log.error("Error checking access to remote resource: " + e.getMessage());
            return false;
        }
    }

    protected boolean isBrowserUpgradeable() {
        String url = Config.coreBaseUrl +
                "/player/isBrowserUpgradeable?displayId=" +
                Config.displayId;

        Log.info("Checking browser upgradeability at " + url);
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.info("Could not determine browser upgradeability - defaulting to true");
                return true;
            }

            byte[] resultBytes = new byte[4];
            int len = con.getInputStream().read(resultBytes);
            String result = new String(resultBytes);
            boolean upgradeable = result.equals("true".substring(0, len));
            Log.info("Browser is " + (upgradeable ? "" : "not ") + "upgradeable");
            return upgradeable;
        } catch (Exception e) {
            Log.error("Error checking browser upgradeability: " + e.getMessage());
            return false;
        }
    }

    protected Properties loadPropertiesFromStream(InputStream stream) throws IOException {
        Properties props = new Properties();

        props.load(stream);
        stream.close();

        return props;
    }

    protected Date parseDate(String value) {
        try {
            return DATE_FORMAT.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isInteger(String value) {
        try {
            new Integer(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
