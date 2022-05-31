package RTPModelForLines.Math;

import RTPModelForLines.Excel.Excel;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Arrays;
import java.util.HashMap;

public class Model {

    Sheet sheet_0;
    Sheet sheet_1;
    Sheet sheet_2;

    int[][] wheelsArrays;   //輪帶表
    int[][] payTable;       //賠付表
    int[] wheelsTotal;      //每軸圖標總數
    int[] wheelsGrid;       //每軸幾格
    int[] freeSpinCount;    //免費次數

    int wheel;              //幾軸
    int wildNum;            //Wild圖標號碼
    int freeSpinNum;        //FreeSpin圖標號碼
    int lineBet;            //押注線數
    int icons;              //圖標總數

    boolean isWildToFreeSpin;   //Wild可否換成FreeSpin

    HashMap<Integer, int[]> iconsCounterMap;    //每個圖標在各軸的總數
    int[][] permutations;

    public Model(String path) {
        Excel excel = new Excel();
        Workbook workbook = excel.getWorkbook(path);
        sheet_0 = workbook.getSheetAt(0); //輪帶表
        sheet_1 = workbook.getSheetAt(1); //賠付表
        sheet_2 = workbook.getSheetAt(2); //參數設定

        HashMap<String, Object> paramMap = excel.getParams(sheet_2);
        wheel =             (int)paramMap.get("Wheel");
        wildNum =           (int)paramMap.get("WildNum");
        freeSpinNum =       (int)paramMap.get("FreeSpinNum");
        lineBet =           (int)paramMap.get("LineBet");
        icons =             (int)paramMap.get("Icons");
        isWildToFreeSpin =  (boolean)paramMap.get("IsWildToFreeSpin");

        wheelsTotal =       (int[])paramMap.get("WheelsTotal");
        wheelsGrid =        (int[])paramMap.get("WheelsGrid");
        freeSpinCount =     (int[])paramMap.get("FreeSpinCount");

        wheelsArrays =      new int[wheel][];
        payTable =          new int[wheel][];
        wheelsArrays =      excel.getWheelArrays(sheet_0, wheelsArrays);
        payTable =          excel.getPayTable(sheet_1, icons, wheel);

        iconsCounterMap = getIconsCounterMap();     //各圖標每軸的總數(加上wild)
//        printIconsCounterMap();
        permutations = getPermutations(wheel);      //基礎中獎 排列組合
//        printPermutations();
    }

    public HashMap<Integer, Double> getEV(){
        HashMap<Integer, Double> result = new HashMap<>();
        double totalEV = 0;
        for(int iconKey : iconsCounterMap.keySet()){
            double iconEV = 0;
            int[] counter = iconsCounterMap.get(iconKey);
            if(iconKey != 0) continue;
            System.out.println("wheelsTotal: " + Arrays.toString(wheelsTotal));

            for(int i = 0; i < permutations.length; i++){
                System.out.println();
                System.out.print("組合: " + Arrays.toString(permutations[i]));
                double ev = 0;
                double prob = 1;
                int payLevel = 0;
                boolean isStop = false;
                //計算排列組合中的機率
                for(int j = 0; j < wheel; j++){
                    //計算該格機率
                    if(permutations[i][j] != 0){
                        prob = prob * (double)counter[j] / wheelsTotal[j];

                        if(!isStop) payLevel = j;
                    }else{

                        prob = prob * (double)(wheelsTotal[j] - counter[j]) / wheelsTotal[j];
                        isStop = true;
                    }
                }
                ev = prob * payTable[iconKey][payLevel] ;
                System.out.print(" payLevel: " + payLevel + " ,pay: " +payTable[iconKey][payLevel]);
                System.out.print("  EV: " + ev);
                iconEV += ev;
            }
            System.out.println();
            System.out.println(iconKey + " : " + iconEV);
            totalEV += iconEV;

        }

        System.out.println("TotalEV: " + totalEV);

        return result;
    }

    //各圖標每軸的總數(加上wild)
    public HashMap<Integer, int[]> getIconsCounterMap(){
        HashMap<Integer, int[]> iconsCounterMap = new HashMap<>();


        for(int i = 0; i < icons; i++){
            int[] iconCounter = new int[wheel];

            for(int j = 0; j < wheelsArrays.length; j++){
                for(int k = 0; k < wheelsArrays[j].length; k++){
                    //
                    if(wheelsArrays[j][k] == i || wheelsArrays[j][k] == wildNum && (i != freeSpinNum || isWildToFreeSpin)){
                        iconCounter[j]++;
                    }
                }
            }
            iconsCounterMap.put(i, iconCounter);
        }

        return iconsCounterMap;
    }

    public void printIconsCounterMap(){
        for(int key : iconsCounterMap.keySet()){
            System.out.print("key " + key + ": ");
            for(int i = 0; i < iconsCounterMap.get(key).length; i++){
                System.out.print(" " + iconsCounterMap.get(key)[i]);
            }
            System.out.println();
        }
    }

    //取得排列組合 [2^wheel][wheel] 0表示沒有 1表示有
    public int[][] getPermutations(int wheel){
        int[][] result = new int[1 << wheel][wheel];
        int grid = 2;// 0 或 1 兩個的排列組合
        for(int i = 0; i < result.length; i++){
            for(int j = 0; j < result[i].length; j++){
                result[i][j] = (i / (int)Math.pow(grid, result[i].length - 1 -j)) % grid;
            }
        }
        return result;
    }

    public void printPermutations(){
        System.out.println("Permutations: ");
        for(int i = 0; i < permutations.length; i++){
            for(int j = 0; j < permutations[i].length; j++){
                System.out.print(permutations[i][j] + " ");
            }
            System.out.println();
        }
    }





}
