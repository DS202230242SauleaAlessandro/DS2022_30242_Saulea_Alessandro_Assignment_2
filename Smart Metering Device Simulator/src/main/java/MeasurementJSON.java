import java.util.UUID;

public record MeasurementJSON(UUID deviceId, long timestamp, float consumption) {
}
