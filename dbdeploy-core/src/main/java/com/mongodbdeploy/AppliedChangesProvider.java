package com.mongodbdeploy;

import java.util.List;

public interface AppliedChangesProvider {
    List<Long> getAppliedChanges();
}
