package com.group.videosharing.dto;
import lombok.Getter;
@Getter
public class ValidationResult {
    private final boolean valid;
    private final String  error;
    private ValidationResult(boolean valid, String error) { this.valid = valid; this.error = error; }
    public static ValidationResult ok()           { return new ValidationResult(true,  null); }
    public static ValidationResult fail(String e) { return new ValidationResult(false, e); }
}
