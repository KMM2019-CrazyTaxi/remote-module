package logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data point data type class. Used for storing a data value with a date.
 *
 * @param <T> Data type point value
 * @author Henrik Nilsson
 */
public class DataPoint<T> {
    private DateTimeFormatter format;

    private LocalDateTime time;
    private T value;

    public DataPoint(T value) {
        this.value = value;
        this.time = LocalDateTime.now();
        this.format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public DataPoint(LocalDateTime time, T value) {
        this.time = time;
        this.value = value;
        this.format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public void setFormat(DateTimeFormatter format) {
        this.format = format;
    }

    public DateTimeFormatter getFormat() {
        return format;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "[" + time.format(format) + "]" + ": " + value;
    }
}
