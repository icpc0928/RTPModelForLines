package RTPModelForLines.Run;

import RTPModelForLines.Math.Model;

public class Run {

    public static void main(String[] args){
        String path = "D:/RTPTesting/RTPModelForLines.xlsx";
        Model model = new Model(path);
        model.getEV();



    }
}
