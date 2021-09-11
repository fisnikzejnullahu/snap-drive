package com.fisnikz.snapdrive;

import javax.inject.Singleton;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

/**
 * @author Fisnik Zejnullahu
 */
public class CustomJsonbConfig {

    @Singleton
    Jsonb jsonConfig() {
        JsonbConfig config = new JsonbConfig()
                .withDateFormat("dd MMM yyyy, HH:mm:ss", null);

        return JsonbBuilder.create(config);
    }
}
