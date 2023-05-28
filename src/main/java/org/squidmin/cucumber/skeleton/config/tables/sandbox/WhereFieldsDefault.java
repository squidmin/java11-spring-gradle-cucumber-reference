package org.squidmin.cucumber.skeleton.config.tables.sandbox;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.squidmin.cucumber.skeleton.config.Field;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "tables.default.where")
@RefreshScope
@Getter
public class WhereFieldsDefault {

    private final List<Field> filters = new ArrayList<>();

}
