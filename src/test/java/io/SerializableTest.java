package io;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class SerializableTest implements Serializable {

    private static final String fileName = "data.txt";

    private class Data implements Serializable {
        private static final long serialVersionUID = 1L;

        int a = 1;
        int b = 2;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return a == data.a && b == data.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

    private class DataV2 extends Data {

    }

    @Test
    public void write() {
        Data data = new Data();
        File file = new File(fileName);
        writeObjectToFile(data, file);
        file.deleteOnExit();
    }

    @Test
    public void read() {
        Data data = new Data();
        File file = new File(fileName);
        writeObjectToFile(data, file);

        try {
            Data readData = readObjectOrNullFromFile(file);
            assertThat(data).isEqualTo(readData);
        } finally {
            file.deleteOnExit();
        }
    }

    @Test
    void readByChildThrowException() {
        Data data = new Data();
        File file = new File(fileName);
        writeObjectToFile(data, file);

        assertThatCode(() -> {
            try {
                DataV2 dataV2 = readObjectOrNullFromFile(file);
                assertThat(data).isEqualTo(dataV2);
            } finally {
                file.deleteOnExit();
            }
        }).isInstanceOf(ClassCastException.class);
    }


    private void writeObjectToFile(Object o, File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(o);
            oos.flush();
        } catch (IOException ignored) {
        }
    }

    private <T> T readObjectOrNullFromFile(File file) {
        try (ObjectInputStream ios = new ObjectInputStream(new FileInputStream(file))) {
            return (T) ios.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
