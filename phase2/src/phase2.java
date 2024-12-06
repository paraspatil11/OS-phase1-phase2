import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class osphase21 {
    private char[][] M = new char[300][4]; // Physical Memory--------------------------
    private char[] IR = new char[4]; // Instruction Register (4 bytes)
    private char[] R = new char[4]; // General Purpose Register (4 bytes)
    private int IC; // Instruction Counter Register (2 bytes)
    private int SI; // Interrupt
    private int TI = 0; // in job if ttc<=tlc then ti=2 else ti=0;
    private int PI; // program error introduse
    boolean C = false; // Toggle (1 byte)
    boolean isterminated = false;
    private char[] buffer = new char[40];
    private final BufferedReader read = new BufferedReader(new FileReader("D:\\OS\\phase2\\src\\input_new.txt"));
    private final BufferedWriter output = new BufferedWriter(new FileWriter("D:\\OS\\phase2\\src\\output1.txt"));
    private int RA;
    private String err;
    private int[] isAllocated;
    private int pageCounter;

    public class PCB {
        int JOBid;
        int TTL, TTC, TLL, TLC;

        void initialize(int id, int x, int y) {
            JOBid = id;
            TTL = x;
            TLL = y;
            TTC = 0;
            TLC = 0;
        }
    }

    private int PTR;
    private PCB p1;
    private int PTE;

    public osphase21() throws IOException {
        p1 = new PCB();
    }

    public void init() { // we initialize the memory.
        for (int i = 0; i < 300; i++) {
            for (int j = 0; j < 4; j++) {
                M[i][j] = ' ';
            }
        }
        for (int i = 0; i < 4; i++) {
            IR[i] = '\0';
            R[i] = '\0';
        }
        SI = 3;
        TI = 0;
        PI = 0;
        pageCounter = 0;
        isAllocated = new int[30];
        for (int i = 0; i < 30; i++) {
            isAllocated[i] = 0;
        }
    }

    private int ALLOCATE() {
        Random r_no = new Random();
        int page;
        page = r_no.nextInt(29);
        System.out.println(page);
        while (isAllocated[page] == 1) {
            page = r_no.nextInt(30);
            System.out.println("The page = " + page);
        }
        isAllocated[page] = 1;
        for (int i = 0; i < 30; i++) {
            System.out.print(isAllocated[i]);
        }
        return page;
    }

    private void ADDRESSMAP(int VA) throws Exception {
        int a, b;
        int frameNo;
        System.out.println(" VA =" + VA);
        if (VA >= 0 && VA <= 99) {
            System.out.println(PTR);
            PTE = PTR + VA / 10;
            frameNo = M[PTE][2] * 10 + M[PTE][3];
            System.out.println(frameNo);
            if (M[PTE][2] == '*' && M[PTE][3] == '*') {
                System.out.println("Error: Page Fault");
                PI = 3;
                SI = 0;
                TI = 0;
                MOS();
            } else {
                RA = frameNo * 10 + (VA / 10);
                System.out.println(RA);
            }
        } else {
            PI = 2; // operand error as VA not correctly specified*
            MOS();
        }
    }

    void TERMINATE(int EM) throws Exception {

        System.out.println("In terminate function....");
        switch (EM) {
            case 0: // No error
                System.out.println("No Error");
                isterminated = true;
                err = "No error";
                break;
            case 1: // Out of data
                System.out.println("Out of data error");
                isterminated = true;
                err = "Out of Data Error";
                break;
            case 2: // Line limit exceeded
                System.out.println("Line limit exceeded");
                isterminated = true;
                err = "Line limit exceeded error";
                break;
            case 3: // Time limit exceeded
                System.out.println("Time limit exceeded");
                isterminated = true;
                err = "Time limit exceeded error";
                break;
            case 4: // opcode error
                System.out.println("Operation code error");
                isterminated = true;
                err = "operation code error";
                break;
            case 5: // Operand error
                System.out.println("Operand error");
                isterminated = true;
                err = "Operand error";
                break;
            case 6: // Invalid page fault
                System.out.println("Invalid page fault");
                isterminated = true;
                err = "Invalid page fault error";
                break;
        }
        output.write("jobID:  " + p1.JOBid);
        output.write("\n  " + err);
        output.write("\nIR :  " + IR[0] + "" + IR[1] + "" + IR[2] + "" + IR[3]);
        output.write("\nIC :  " + IC);
        output.write("\nTTC:  " + p1.TTC);
        output.write("\nTLC:  " + p1.TLC);
        output.write("\n");
        output.write("\n");
    }

    public void STARTEXECUTION() throws Exception {
        IC = 0;
        EXECUTEUSERPROGRAM();
    }

    public void LOAD() throws Exception { // load the data in buffer of size 40 bytes

        String line;

        char[] temp = new char[4];
        do {
            line = read.readLine();
            if (line == null) {
                break; // End of file
            }
            Arrays.fill(buffer, '\0'); // we clear the buffer
            for (int i = 0; i < 40 && i < line.length(); i++) {
                buffer[i] = line.charAt(i);
            }

            if (buffer[0] == '$' && buffer[1] == 'A' && buffer[2] == 'M' && buffer[3] == 'J') {
                System.out.println("going for intializing the memory");

                System.arraycopy(buffer, 4, temp, 0, 4);
                p1.JOBid = Integer.parseInt(new String(temp));
                System.arraycopy(buffer, 8, temp, 0, 4);
                p1.TTL = Integer.parseInt(new String(temp));
                System.arraycopy(buffer, 12, temp, 0, 4);
                p1.TLL = Integer.parseInt(new String(temp));
                p1.TTC = 0;
                p1.TLC = 0;
                init();
                p1.initialize(p1.JOBid, p1.TTL, p1.TLL);
                PTR = ALLOCATE() * 10;
                for (int i = 0; i < 10; i++) {
                    M[PTR + i][0] = '0';
                    M[PTR + i][1] = ' ';
                    M[PTR + i][2] = '*';
                    M[PTR + i][3] = '*';
                }
            } else if (buffer[0] == '$' && buffer[1] == 'D' && buffer[2] == 'T' && buffer[3] == 'A') {
                System.out.println("going  to excution");
                STARTEXECUTION();
            } else if (buffer[0] == '$' && buffer[1] == 'E' && buffer[2] == 'N' && buffer[3] == 'D') {
                continue;
            } else {
                int k = 0;
                int curptr;
                int s = PTR;
                while (M[s][0] != '0') // we are updating page table here
                {
                    s++;
                }
                int page = ALLOCATE();
                M[PTR + s][0] = '1';
                M[PTR + s][2] = (char) ((page / 10) + '0');
                M[PTR + s][3] = (char) ((page % 10) + '0');
                // pageCounter += 1;
                curptr = page * 10;
                for (int x = 0; x < 10; ++x) {
                    for (int j = 0; j < 4; j++) {
                        M[curptr][j] = buffer[k];
                        k++;
                    }
                    if (k == 40 || buffer[k] == '\n') {
                        break;
                    }
                    curptr++;
                }

                for (int i = 0; i < 300; i++) {
                    System.out.print("M[" + i + "] :");
                    for (int j = 0; j < 4; j++) {
                        System.out.print(M[i][j]);
                    }
                    System.out.println();
                }
            }
        } while (true);

        output.close();

    }

    public void EXECUTEUSERPROGRAM() throws Exception {
        isterminated = false;

        while (!isterminated) {
            System.out.println(" IC " + IC);
            ADDRESSMAP(IC);
            System.out.println("IR ");
            for (int i = 0; i < 4; i++) {
                IR[i] = M[RA][i];
                System.out.print(IR[i] + " ");
            }
            IC = IC + 1;
            if (!(IR[2] >= '0' && IR[2] <= '9') && (IR[3] >= '0' && IR[3] <= '9')) {
                PI = 2;
                TI = 0;
                SI = 0;
                MOS();
            }

            if (IR[0] == 'G' && IR[1] == 'D') {
                // p1.TTC++;
                int operand = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
                ADDRESSMAP(operand);
                SI = 1;

                MOS(); // here every time interupt comes machine switch to master mode for excution of
                // commands
            } else if (IR[0] == 'P' && IR[1] == 'D') {
                int operand = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
                ADDRESSMAP(operand);
                SI = 2;
                PI = 0;
                // p1.TTC++;
                MOS();
            } else if (IR[0] == 'L' && IR[1] == 'R') {
                // p1.TTC++;
                int operand = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
                ADDRESSMAP(operand);
                for (int k = 0; k <= 3; k++) { // load in the register r first four bit of the block
                    R[k] = M[RA][k];
                }
                System.out.println();
            } else if (IR[0] == 'S' && IR[1] == 'R') {
                int operand = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
                ADDRESSMAP(operand);
                // p1.TTC++;
                for (int k = 0; k <= 3; k++) { // load in the register r first four bit of the block
                    M[RA][k] = R[k];
                }
                System.out.println();
            } else if (IR[0] == 'C' && IR[1] == 'R') {
                // p1.TTC++;
                int operand = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
                ADDRESSMAP(operand);
                int count = 0;
                for (int k = 0; k < 4; k++) {
                    if (M[RA][k] == R[k]) {
                        count++;
                    }
                }
                if (count == 4) {
                    C = true;
                }
            } else if (IR[0] == 'B' && IR[1] == 'T') {
                // p1.TTC++;
                if (C == true) {
                    int a = 0;
                    for (int i = 0; i < 2; i++) {
                        a = a * 10 + (IR[i + 2] - 48);
                    }
                    IC = a; // here it jump on that block for next excution
                }
            } else if (IR[0] == 'H') {
                // // p1.TTC++;
                SI = 3;
                TI = 0;
                PI = 0;
                MOS();
            } else {
                PI = 1;
                MOS();
                isterminated = true;
            }
            SIMULATION();

            Arrays.fill(IR, '\0');
        }

    }

    void SIMULATION() throws Exception {
        p1.TTC++;
        if (p1.TTC == p1.TTL) {
            TI = 2;
            MOS();
            // break;
        }
    }

    void READ() throws Exception {
        int k = 0;
        Arrays.fill(buffer, '\0');
        String line;
        line = read.readLine();
        if (line == null
                || (line.charAt(0) == '$' && line.charAt(1) == 'E' && line.charAt(2) == 'N' && line.charAt(3) == 'D')) {
            TERMINATE(1);
        } else {
            Arrays.fill(buffer, '\0'); // we clear the buffere
            for (int a = 0; a < 40 && a < line.length(); a++) {
                buffer[a] = line.charAt(a);
            }
            for (int l = 0; l < 10; ++l) {
                for (int j = 0; j < 4; ++j) {
                    M[RA][j] = buffer[k];
                    k++;
                }
                if (k == 40) {
                    break;
                }
                RA++;
            }
        }
    }

    void WRITE() throws Exception {
        Arrays.fill(buffer, '\0'); // clear the buffer
        p1.TLC++;
        if (p1.TLC > p1.TTL) {
            TERMINATE(2);
        }
        int k = 0;
        for (int l = 0; l < 10; l++) {
            for (int j = 0; j < 4; ++j) {
                buffer[k] = M[RA][j];
                if (buffer[k] != '\0') {
                    output.write(buffer[k]);
                }
                k++;
            }

            if (k == 40) {
                break;
            }
            RA++;
        }
        output.write("\n");
    }

    public void MOS() throws Exception {
        if (TI == 0) {
            if (SI == 1) {
                READ();
            }
            if (SI == 2) {
                WRITE();
            }
            if (SI == 3) {
                TERMINATE(0);
                output.write("\n \n");

            }
            if (PI == 1) {
                TERMINATE(4);
            }
            if (PI == 2) {
                TERMINATE(5);
            }
            if (PI == 3) {
                PI = 0;
                if ((IR[0] == 'G' && IR[1] == 'D') || (IR[0] == 'S' && IR[1] == 'R')) {
                    // int r;
                    int virtualAddress = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
                    int pageTableEntry = PTR + (virtualAddress / 10);
                    int page = ALLOCATE();
                    M[pageTableEntry][0] = '1';
                    M[pageTableEntry][2] = (char) ((page / 10) + '0');
                    M[pageTableEntry][3] = (char) ((page % 10) + '0');
                    // pageCounter += 1;
                    // IC--;
                    System.out.println("Valid Page Fault, page frame = " + page);
                    System.out.println("PTR = " + PTR);
                    for (int i = 0; i < 300; i++) {
                        System.out.print("M[" + i + "] :");
                        for (int j = 0; j < 4; j++) {
                            System.out.print(M[i][j]);
                        }

                        System.out.println();
                    }
                    System.out.println();
                    IC--;
                    EXECUTEUSERPROGRAM();
                } else {
                    TERMINATE(6);
                }
            }
        } else {
            if (SI == 1) {
                TERMINATE(3);
            }
            if (SI == 2) {
                WRITE();
                TERMINATE(3);
            }
            if (SI == 3) {
                TERMINATE(0);
                // output.write("\n \n");
            }
            if (PI == 1) {

                TERMINATE(3);
                TERMINATE(4);
            }
            if (PI == 2) {
                TERMINATE(3);
                TERMINATE(5);

            }
            if (PI == 3) {
                TERMINATE(3);
            }
        }

    }

    public static void main(String[] args) throws IOException {
        osphase21 t2 = new osphase21(); // Potential IOException handled by the main method

        try {
            t2.LOAD();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
        }

    }

}