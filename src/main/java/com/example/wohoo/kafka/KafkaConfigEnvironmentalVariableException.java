package com.example.wohoo.kafka;

public class KafkaConfigEnvironmentalVariableException extends Exception {
    final String missingEnvironmentalVariable;

    /**
     * Constructs a new 'missing environmental variable' exception with a message
     * indicating which variable is missing.
     * @param missingEnvironmentalVariable the name of the missing environmental variable
     */
    public KafkaConfigEnvironmentalVariableException(String missingEnvironmentalVariable) {
        super(String.format("Environmental variable '%s' must be set.", missingEnvironmentalVariable));
        this.missingEnvironmentalVariable = missingEnvironmentalVariable;
    }

    public String getMissingEnvironmentalVariable() {
        return this.missingEnvironmentalVariable;
    }
}
