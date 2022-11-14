package solution;

public class MainLoadFilesWithoutMThreads {
    public static void main(String[] args) throws Exception {

        //Запись в 4 файла однопоточно
        LoadToFilesWithoutMThreads writer =
                new LoadToFilesWithoutMThreads(99,999,30);
        writer.writeFiles();
    }
}
