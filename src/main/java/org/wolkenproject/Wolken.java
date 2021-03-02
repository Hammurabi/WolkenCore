package org.wolkenproject;

import org.apache.commons.cli.*;
import org.wolkenproject.core.Address;
import org.wolkenproject.core.Context;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.encoders.CryptoLib;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.FileService;
import org.wolkenproject.utils.Logger;

import java.io.IOException;

public class Wolken {
    public static void main(String args[]) throws ParseException, WolkenException, IOException {
        CryptoLib.initialize();

        Options options = new Options();
        options.addOption("dir", true, "set the main directory for wolken, otherwise uses the default application directory of the system.");
        options.addOption("enable_testnet", true, "set the testnet to enabled/disabled.");
        options.addOption("enable_mining", true, "set the node to a mining node.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        FileService mainDirectory = FileService.appDir();
        if (cmd.hasOption("dir")) {
            FileService dir = new FileService(cmd.getOptionValue("dir"));
            if (dir.exists()) {
                mainDirectory = dir;
            } else {
                Logger.faterr("provided directory '" + cmd.getOptionValue("dir") + "' does not exist.");
                return;
            }
        }

        boolean isTestNet = false;
        if (cmd.hasOption("enable_testnet")) {
            String value = cmd.getOptionValue("enable_testnet").toLowerCase();
            // we could parse into a boolean here
            if (value.equals("true")) {
                isTestNet = true;
            } else if (value.equals("false")) {
                isTestNet = false;
            } else {
                Logger.faterr("provided argument '-enable_testnet " + cmd.getOptionValue("enable_testnet") + "' is invalid.");
                return;
            }
        }

        mainDirectory = mainDirectory.newFile("Wolken");
        if (!mainDirectory.exists())
        {
            mainDirectory.makeDirectory();
        }

        Address address[] = new Address[8];

        if (cmd.hasOption("enable_mining")) {
            String value = cmd.getOptionValue("enable_mining").toLowerCase();
            value = value.substring(1, value.length() - 1);

            String addresses[] = value.split(",");
            address = new Address[addresses.length];

            for (String b58 : addresses) {
                if (!Address.isValidAddress(Base58.decode(b58))) {
                    throw new WolkenException("invalid address '" + b58 + "' provided.");
                }
            }
        }

        Context context = new Context(mainDirectory, isTestNet, address);
    }
}
