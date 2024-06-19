CC = gcc
CFLAGS = -Wall -g

TARGET = main

SRC = $(TARGET).c

all: $(TARGET)

$(TARGET): $(SRC)             
	$(CC) $(CFLAGS) -o $@ $<


%.o: %.c
	$(CC) $(CFLAGS) -c $<

run: $(TARGET)
	./$(TARGET) 10 # Example argument, can be changed

clean:
	rm -f $(TARGET) fifo1 fifo2


