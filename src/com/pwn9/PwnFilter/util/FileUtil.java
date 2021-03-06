package com.pwn9.PwnFilter.util;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.plugin.Plugin;

import java.io.*;

/**
 * Helpers for File Handling
 * User: ptoal
 * Date: 13-11-25
 * Time: 1:04 PM

 */

public class FileUtil {

    /**
     * Get a File object pointing to the named configuration in the configured
     * Rule Directory.
     *
     * @param dir File object for the directory containing the file.
     * @param fileName Name of File to load
     * @param createFile Create the file if it doesn't exist, using a template
     *                   from the plugin resources directory.
     * @return File object for requested config, or null if not found.
     */
    public static File getFile(File dir, String fileName, boolean createFile) {
        try {
            File ruleFile = new File(dir,fileName);
            if (ruleFile.exists()) {
                return ruleFile;
            } else {
                if (createFile) {
                    if (!ruleFile.getParentFile().exists() && !ruleFile.getParentFile().mkdirs()) {
                        LogManager.logger.warning("Unable to create directory for:" + fileName);
                        return null;
                    }
                    if (copyTemplate(ruleFile, fileName)) {
                        return ruleFile;
                    } else {
                        LogManager.logger.warning("Unable to find or create file:" + fileName);
                        return null;
                    }
                }
            }

        } catch (IOException ex) {
            LogManager.logger.warning("Unable to find or create file:" + fileName);
            LogManager.getInstance().debugLow(ex.getMessage());
        } catch (SecurityException ex) {
            LogManager.logger.warning("Insufficient Privileges to create file: " + fileName);
            LogManager.getInstance().debugLow(ex.getMessage());
        }
        return null;
    }

    /**
     * Copy a file from the plugin resources to the local filesystem.
     * @param destFile The File object that points to the destination file
     * @param configName The name of the configuration file to look for in the plugin resources.
     * @return Success or failure.
     * @throws IOException
     * @throws SecurityException
     */
    public static boolean copyTemplate(File destFile, String configName) throws IOException, SecurityException {
        Plugin plugin = PwnFilter.getInstance();

        InputStream templateFile;

        templateFile = plugin.getResource(configName);
        if (templateFile == null) {
            // Create an empty file.
            return destFile.mkdirs() && destFile.createNewFile();
        }
        if (destFile.createNewFile()) {
            BufferedInputStream fin = new BufferedInputStream(templateFile);
            FileOutputStream fout = new FileOutputStream(destFile);
            byte[] data = new byte[1024];
            int c;
            while ((c = fin.read(data, 0, 1024)) != -1)
                fout.write(data, 0, c);
            fin.close();
            fout.close();
            LogManager.logger.info("Created file from template: " + configName);
            return true;
        } else {
            LogManager.logger.warning("Failed to create file from template: " + configName);
            return false;
        }
    }


}
