import machineLearnings.WithoutTeacher;

import java.io.FileNotFoundException;

public class main {
    public static void main(String[] args) throws FileNotFoundException {
        WithoutTeacher ml = new WithoutTeacher("src/resources/CancersWithoutTeacher.csv", 4);
    }
}