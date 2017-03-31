import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateRecords {
    public static void main(String[] args) throws IOException {
        System.out.println("Generating records ...");
        File file = new File("./records.txt");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        BufferedWriter bf = new BufferedWriter(writer);
        for (int i = 0; i < 100000000; i++) {
            bf.write((int)(Math.random() * 3000000) + "\t" + (int)(Math.random() * 10000) + "\t" + (int)(Math.random() * 6));
            bf.newLine();
        }
        bf.flush();
        bf.close();
        System.out.println("Generated records: ./records.txt !");
    }
}
