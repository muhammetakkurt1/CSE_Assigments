CC = gcc
CFLAGS = -Wall -Werror -g

TARGET = gtuStudentGrades
SOURCES = main.c

all: $(TARGET)

$(TARGET): $(SOURCES)
	$(CC) $(CFLAGS) $(SOURCES) -o $(TARGET)

clean:
	rm -f $(TARGET) *.o
