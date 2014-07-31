package com.mongodbdeploy;

import com.mongodbdeploy.scripts.ChangeScript;

import java.util.List;

public interface ChangeScriptApplier {
    void apply(List<ChangeScript> changeScript);
}
