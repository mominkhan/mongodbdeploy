package com.mongodbdeploy;

import com.mongodbdeploy.scripts.ChangeScript;

import java.util.List;

public interface AvailableChangeScriptsProvider {
    List<ChangeScript> getAvailableChangeScripts();
}
