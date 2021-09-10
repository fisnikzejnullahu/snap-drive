package com.fisnikz.snapdrive;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import javax.enterprise.context.Dependent;

/**
 * @author Fisnik Zejnullahu
 */
@Dependent
public class UpperCaseWithUnderScoresHibernateNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return null;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return null;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        String newName = convert(identifier.getText());
        return newName.endsWith("S") ? Identifier.toIdentifier(newName) : Identifier.toIdentifier(newName + "S");
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return null;
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        String newName = convert(identifier.getText());
        return Identifier.toIdentifier(newName);
    }

    String convert(String name) {
        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        return name
                .replaceAll(regex, replacement)
                .toUpperCase();
    }
}
