import java.io.*;
import java.util.Arrays;

public class phase1 {
    private char[][] M = new char[100][4]; // Physical Memory
    private char[] IR = new char[4]; // Instruction Register (4 bytes)
    private char[] R = new char[4]; // General Purpose Register (4 bytes)
    private int IC; // Instruction Counter Register (2 bytes)
    private int SI; // Interrupt
    boolean C = false; // Toggle (1 byte)
    private char[] buffer = new char[40];
    private final BufferedReader read = new BufferedReader(new FileReader("D:\\OS\\phase1\\src\\input.txt"));
    private final BufferedWriter output = new BufferedWriter(new FileWriter("D:\\OS\\phase1\\src\\output.txt"));

    phase1() throws IOException {
    }

    public void init() { // we initialize the memory.
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 4; j++) {
                M[i][j] = 0;
            }
        }

        IR[0] = 0;
        R[0] = 0;
        IC = 0; // Ensure IC is initialized
        C = false; // Ensure C is initialized
    }

    public void LOAD() throws Exception { // load the data in buffer of size 40 bytes
        String line;
        int x = 0; // Memory index to store data

        do {
            line = read.readLine();
            if (line == null) {
                break;
            }
            Arrays.fill(buffer, '\0'); // Clear the buffer
            for (int i = 0; i < 40 && i < line.length(); i++) {
                buffer[i] = line.charAt(i);
            }

            if (buffer[0] == '$' && buffer[1] == 'A' && buffer[2] == 'M' && buffer[3] == 'J') {
                System.out.println("Initializing memory...");
                init(); // Call init to reset the memory and registers
                continue;

            } else if (buffer[0] == '$' && buffer[1] == 'D' && buffer[2] == 'T' && buffer[3] == 'A') {
                IC = 0; // Reset the Instruction Counter
                System.out.println("Starting execution...");
                MOS_STARTEXECUTION();
                continue;

            } else if (buffer[0] == '$' && buffer[1] == 'E' && buffer[2] == 'N' && buffer[3] == 'D') {
                x = 0; // Reset memory index for next job
                MEMORYCONTENT();
                LOAD(); // Load next job if present
            } else {
                int k = 0; // Buffer index to read instructions

                while (x < 100 && k < 40) {
                    for (int j = 0; j < 4; j++) {
                        if (k < buffer.length && buffer[k] != '\0') {
                            M[x][j] = buffer[k];
                            k++;
                        } else {
                            break; // Exit inner loop if end of buffer or null character
                        }
                    }
                    x++; // Move to next memory row
                    if (k >= buffer.length) {
                        break; // Exit loop if end of buffer
                    }
                }
            }
        } while (true);

        // if (output != null) {
        // output.close();
        // }
        closeResources();
    }

    public void MEMORYCONTENT() {
        System.out.println("Memory Contents:");
        for (int i = 0; i < 100; i++) {
            System.out.print(i + ": "); // Print newline
            for (int j = 0; j < 4; j++) {
                System.out.print(M[i][j]);
            }
            System.out.println(); // Print newline after each memory row
        }
    }

    public void MOS_STARTEXECUTION() {
        int IC = 0;
        EXECUTEUSERPROGRAM();
    }

    public void EXECUTEUSERPROGRAM() {
        // int i = 0;
        while (true) {
            for (int i = 0; i < 4; i++) { // load the command in the ir ex gd10 pd10 but one at time after that ic get
                // incremented
                IR[i] = M[IC][i];
            }
            IC++;
            if (IR[0] == 'L' && IR[1] == 'R') {
                int j = IR[2] - 48;// converting the character into the int
                j = j * 10 + (IR[3] - 48);// here we define the blocks
                for (int k = 0; k <= 3; k++) {// load in the register r first four bit of the block
                    R[k] = M[j][k];
                }
                System.out.println();
            } else if (IR[0] == 'S' && IR[1] == 'R') {

                int j = IR[2] - 48;// converting the character into the int
                j = j * 10 + (IR[3] - 48);// here we define the blocks
                for (int k = 0; k <= 3; k++) {// load in the register r first four bit of the block
                    M[j][k] = R[k];
                }
                System.out.println();
            } else if (IR[0] == 'C' && IR[1] == 'R') {
                int j = IR[2] - 48;// converting the character into the int
                j = j * 10 + (IR[3] - 48);// here we define the blocks

                int count = 0;
                for (int k = 0; k < 4; k++) {
                    if (M[j][k] == R[k]) {
                        count++;
                    }
                }
                if (count == 4) {
                    C = true;
                } else {
                    C = false;
                }
            } else if (IR[0] == 'B' && IR[1] == 'T') {
                if (C == true) {
                    int j = IR[2] - 48;// converting the character into the int
                    j = j * 10 + (IR[3] - 48);
                    IC = j;// here it jump on that block for next excution
                }
            } else if (IR[0] == 'G' && IR[1] == 'D') {
                SI = 1;
                MOS();// here every time interupt comes machine switch to master mode for excution of
                // commands
            } else if (IR[0] == 'P' && IR[1] == 'D') {
                SI = 2;
                MOS();
            } else if (IR[0] == 'H') {
                SI = 3;
                MOS();
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        phase1 t = new phase1();
        t.LOAD();
    }

    public void MOS() {
        switch (SI) {
            case 1:
                Read();
                break;
            case 2:
                Write();
                break;
            case 3:
                Terminate();
                break;
            default:
                break;
        }
    }

    public void Read() {
        int i = IR[2] - 48;// converting the charecter into the int
        i = i * 10;
        int k = 0;
        String line = null;
        Arrays.fill(buffer, '\0');
        try {
            line = read.readLine();
        } catch (IOException e) {
            System.out.println(e);
        }

        Arrays.fill(buffer, '\0');// we clear the buffere
        for (int a = 0; a < 40 && a < line.length(); a++) {
            buffer[a] = line.charAt(a);
        }
        for (int l = 0; l < 10; ++l) {
            for (int j = 0; j < 4; ++j) {
                M[i][j] = buffer[k];
                k++;
            }
            if (k == 40) {
                break;
            }
            i++;
        }

    }

    public void Write() {
        Arrays.fill(buffer, '\0');// clear the buffer

        int i = IR[2] - 48;// converting the charecter into the int
        i = i * 10;

        System.out.println(i);
        int k = 0;
        for (int l = 0; l < 10; ++l) {
            for (int j = 0; j < 4; ++j) {
                buffer[k] = M[i][j];
                if (buffer[k] != '\0') {
                    try {
                        output.write(buffer[k]);
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
                k++;
            }
            if (k == 40) {
                break;
            }
            i++;
        }

        try {
            output.write("\n");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void Terminate() {
        try {
            output.write("\n");
            output.write("\n");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void closeResources() {
        try {
            if (read != null) {
                read.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

}