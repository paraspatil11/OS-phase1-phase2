# OS-phase1-phase2
*Operating System Simulator for Phase 1 and Phase 2*

phase1.java:Implements basic memory management.
Simulates instruction execution with functionalities like load, store, compare, and branch operations.
Reads input from a file, processes instructions, and outputs results.

phase2.java:Extends functionality with page table implementation and virtual to physical address translation.
Handles interrupts (SI, TI, PI) and error management, such as page faults and operand errors.
Includes PCB (Process Control Block) for job management.

How to Use:
Place input files in the paths specified in the code:
Phase 1: D:\OS\phase1\src\input.txt
Phase 2: D:\OS\phase2\src\input_new.txt

Run the Java files:
javac phase1.java
java phase1

javac phase2.java
java phase2

Output will be saved to:
Phase 1: D:\OS\phase1\src\output.txt
Phase 2: D:\OS\phase2\src\output1.txt
