CC=gcc
CFLAGS=-pthread
TARGET=ParkingSystem

all: $(TARGET)

$(TARGET): main.c
	$(CC) $(CFLAGS) $^ -o $@

clean:
	rm -f $(TARGET)

.PHONY: all clean
