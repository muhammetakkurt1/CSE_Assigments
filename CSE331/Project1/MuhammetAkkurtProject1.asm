.data

prompt_row: .asciiz "Enter the number of rows:"
prompt_column: .asciiz "Enter the number of column:"
prompt_data: .asciiz "Enter row data: "

prompt_second: .asciiz "Enter the number of seconds:"
newline: .asciiz "\n"



.text

.globl main



main:

    # Prompt user for the number of rows for the map.

    li $v0, 4

    la $a0, prompt_row

    syscall



    # Read number of rows from the user input.

    li $v0, 5

    syscall

    move $t0, $v0 # Store number of rows in $t0.

  
    # Prompt user for the number of columns for the map.

    li $v0, 4

    la $a0, prompt_column

    syscall


    # Read number of columns from the user input.

    li $v0, 5

    syscall

    move $t1, $v0 # Store number of columns in $t1.

    # Prompt user for number of seconds.

    li $v0, 4

    la $a0, prompt_second

    syscall

    # Read number of seconds from the user input.

    li $v0, 5

    syscall

    move $s3, $v0 # Store number of seconds in $s3.
    
	
    # Calculate the total size needed for the map (rows * columns).

    mul $t2, $t0, $t1 # $t2 holds the total size required for the map.



    # Allocate space for the map in memory.

    li $v0, 9 # sbrk system call for memory allocation.

    move $a0, $t2 # Allocate $t2 bytes

    syscall

    move $t3, $v0 # $t3 now points to the beginning of the allocated memory for the map.
    move $s1, $t3 # $s1 also points to the start of the map, for later reference.

    
    # Allocate space for the result_map in memory.

    li $v0, 9 # sbrk system call again for memory allocation.

    move $a0, $t2 # Allocate memory of size $t2 bytes for result_map.

    syscall

    move $t7, $v0 # $t7 points to the beginning of the allocated memory for the result_map.
    move $s0, $t7 # $s0 also points to the start of the result_map, for later reference.	

    # Initialize counters for reading map data from the user.

    li $t4, 0 # Counter for rows.
    addi $t8, $t2, 0 # $t8 is set to the total size of the map.
    
    # Divide the seconds by 4 and store the remainder to decide mode of operation.
    li $s4, 4
     div $s3, $s4    # Divide the given seconds by 4.
     mfhi $s4	     # Move remainder from $hi to $s4.
     
    # Loop to read each row of data from the user.
 read_row_loop:

    bge $t4, $t0, check_mode # Exit loop if all rows are read.



    # Prompt user for row data.

    li $v0, 4

    la $a0, prompt_data

    syscall



    # Loop to read each column of data from the user.

    li $t5, 0  # Column counter.

read_col_loop:

    bge $t5, $t1, end_row_read # Exit loop if all columns are read.



     # Read a single character from the user.

    li $v0, 12

    syscall

    sb $v0, 0($t3) # Store the character in the map.


     # Update pointers and counters.
    addiu $t3, $t3, 1 # Move to the next space in memory

    addiu $t5, $t5, 1 # Increment column count

    j read_col_loop # Jump back to read the next column.



end_row_read:

    # After each row, we should consume the newline character

    li $v0, 12

    syscall


    addiu $t4, $t4, 1 # Increment row count

    j read_row_loop  #jump back to read the next row.
  
check_mode:
     
     li $s3, 1    
     beq $s4, $s3, print_init_map # If remainder is 1, print initial map.
     
     li $s3, 0
     beq $s4, $s3, fill_result_map_only	# If remainder is 0, fill result map with 'O'.

     li $s3, 2
     beq $s4, $s3, fill_result_map_only	# If remainder is 2, also fill result map with 'O'.
     
     li $s3, 3
     beq $s4, $s3, fill_result_map_detonate # If remainder is 3, fill and detonate bombs.	
     
# Start of the print_init_map subroutine
print_init_map:
	move $s0,$s1	# Copy the start address of the original map to the pointer used for printing.
	
	j print_map
				
# Start of the fill_result_map_only subroutine:
fill_result_map_only:

    li $t6, 79 # Load the ASCII value for 'O' into $t6.

    sb $t6, 0($t7)  # Store 'O' at the current position of the result_map pointed by $t7.

    addiu $t7, $t7, 1 # Increment the result_map pointer to the next cell.

    addi $t8, $t8, -1 # Decrement the cell counter $t8.

    bgtz $t8, fill_result_map_only  # If there are more cells to fill, repeat the process.

print_full_O_map:
	j print_map		# Jump to print_map subroutine after filling the result_map.

# Start of the fill_result_map_detonate subroutine:
fill_result_map_detonate:

    li $t6, 79  # Load the ASCII value for 'O' into $t6.

    sb $t6, 0($t7)  # Store 'O' at the current position of the result_map pointed by $t7.

    addiu $t7, $t7, 1 # Increment the result_map pointer to the next cell.

    addi $t8, $t8, -1 # Decrement the cell counter $t8.

    bgtz $t8, fill_result_map_detonate # If there are more cells to fill, repeat the process.
    
# Start of the detonate_bombs subroutine:
detonate_bombs:

    li $s3, 0            # Initialize the row counter to 0.



    move $s2, $s0        # Copy the start address of the result_map to $s2 for tracking current cell during detonation.



    addi $s6, $t0, -1    # Store the index of the last row in $s6.

    addi $s7, $t1, -1    # Store the index of the last column in $s7.


# Loop through each row to check for bombs:
detonate_row_loop:

    li $s4, 0            # Initialize the column counter to 0.


# Loop through each column in the current row:
detonate_col_loop:

    lb $t9, 0($s1)       # Load the current cell character from the map into $t9.



    li $t6, 79           # ASCII value for 'O'.

    bne $t9, $t6, skip_bomb  # If current cell is not 'O' (a bomb), skip to the next cell.



    # If bomb is found, detonate and mark on the result_map with '.'

    li $t7, 46           # ASCII value for '.'



    sb $t7, 0($s2)       # Mark the current cell on the result_map with '.'



    # If not at the first row, mark the cell above as detonated:

    bgtz $s3, fill_above

    j skip_above

# Marks the cell above the current cell if a bomb is found.
fill_above:

    sub $s5, $s2, $t1    # Calculate the address of the cell above the current cell.

    sb $t7, 0($s5)       # Store the '.' character at the address of the cell above to mark it.


# Skip to this label if there is no cell above to mark.
skip_above:

    # If we are not at the last row, there is a cell below to mark.

    blt $s3, $s6, fill_below     # If the current row is less than the last row index, jump to fill_below.

    j skip_below         # Otherwise, jump to skip_below.	

# Marks the cell below the current cell if a bomb is found.
fill_below:

    add $s5, $s2, $t1    # Calculate the address of the cell below the current cell.

    sb $t7, 0($s5)       # Store the '.' character at the address of the cell below to mark it.


# Skip to this label if there is no cell below to mark or after marking the cell below.
skip_below:

    # If we are not at the first column, there is a cell to the left to mark.

    bgtz $s4, fill_left   # If the current column is greater than 0, jump to fill_left.

    j skip_left    # Otherwise, jump to skip_left.
    
# Marks the cell to the left of the current cell if a bomb is found.
fill_left:

    sub $s5, $s2, 1      # Calculate the address of the cell to the left of the current cell.

    sb $t7, 0($s5)       # Store the '.' character at the address of the cell to the left to mark it.


# Skip to this label if there is no cell to the left to mark or after marking the cell to the left.
skip_left:

     # If we are not at the last column, there is a cell to the right to mark.

    blt $s4, $s7, fill_right  # If the current column is less than the last column index, jump to fill_right.

    j skip_right   # Otherwise, jump to skip_right.

# Marks the cell to the right of the current cell if a bomb is found.
fill_right:

    add $s5, $s2, 1      # Calculate the address of the cell to the right of the current cell.

    sb $t7, 0($s5)       # Store the '.' character at the address of the cell to the right to mark it.


# Skip to this label if there is no cell to the right to mark or after marking the cell to the right.
skip_right:

# No action needed, acts as a placeholder for the jump if the cell to the right is not marked.


# Skip to this label if the current cell does not contain a bomb ('O').
skip_bomb:

    addi $s1, $s1, 1     # Move to the next cell in the original map.

    addi $s2, $s2, 1     # Move to the next cell in the result_map.

    addi $s4, $s4, 1      # Increment the column counter.

    blt $s4, $t1, detonate_col_loop # If not all columns are processed, repeat the column loop.



    addi $s3, $s3, 1     # Increment the row counter.

    blt $s3, $t0, detonate_row_loop # If not all rows are processed, repeat the row loop.

# Subroutine to print the map.
print_map:

    li $t4, 0 # Initialize the row counter for printing.

print_row_loop:

    bge $t4, $t0, end_program # If we have printed all rows, end the program.

     # Loop through each column for printing the current row.

    li $t5, 0 # Initialize the column counter for printing.

print_col_loop:

    bge $t5, $t1, end_row_print # If we have printed all columns in the row, prepare to print a new line.


    lb $a0, 0($s0) # Load the byte to print from the result_map.

    li $v0, 11 # System call for printing a character.

    syscall



    addiu $s0, $s0, 1 # Move to the next space in the result_map.

    addiu $t5, $t5, 1 # Increment the column counter.

    j print_col_loop # Continue printing the current row.



end_row_print:

     # Print a newline after each row.

    li $v0, 4

    la $a0, newline

    syscall



    addiu $t4, $t4, 1 # Increment the row counter.

    j print_row_loop # Continue printing the next row.


# Subroutine to end the program.
end_program:

    li $v0, 10  # System call for exit.
 
    syscall
