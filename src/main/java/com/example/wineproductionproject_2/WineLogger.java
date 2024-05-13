package com.example.wineproductionproject_2;


import org.apache.log4j.Logger;

public class WineLogger {
    private static final Logger LOGGER = Logger.getLogger(LogInSceneController.class);

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static WineLogger getInstance(){return new WineLogger();}
}
