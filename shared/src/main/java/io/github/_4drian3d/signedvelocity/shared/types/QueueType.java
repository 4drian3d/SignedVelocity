package io.github._4drian3d.signedvelocity.shared.types;

public enum QueueType {
  CHAT("CHAT_RESULT"),
  COMMAND("COMMAND_RESULT");

  private final String value;

  QueueType(final String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static QueueType getOrThrow(final String type) {
    return switch (type) {
      case "CHAT_RESULT" -> CHAT;
      case "COMMAND_RESULT" -> COMMAND;
      default -> throw new IllegalArgumentException("Invalid source " + type);
    };
  }
}
