package io.github._4drian3d.signedvelocity.shared.types;

public enum ResultType {
  ALLOWED("ALLOWED"),
  MODIFY("MODIFY"),
  CANCEL("CANCEL");

  private final String value;

  ResultType(final String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public static ResultType getOrThrow(final String type) {
    return switch (type) {
      case "ALLOWED" -> ALLOWED;
      case "MODIFY" -> MODIFY;
      case "CANCEL" -> CANCEL;
      default -> throw new IllegalArgumentException("Invalid result " + type);
    };
  }
}
