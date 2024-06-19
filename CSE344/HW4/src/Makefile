CC=gcc
CFLAGS=-Wall -g
LDFLAGS=-pthread
TARGET=MWCp

all: $(TARGET)              

$(TARGET): 1901042644_main.c            
	$(CC) $(CFLAGS) $(LDFLAGS) 1901042644_main.c -o $(TARGET)

clean:                     
	rm -f $(TARGET)

.PHONY: clean
