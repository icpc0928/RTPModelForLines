package RTPModelForLines.Excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Excel {

    String xls = ".xls";
    String xlsx = ".xlsx";

    //取得工作表
    public Workbook getWorkbook(String path){
        Workbook wb = null;
        if(path == null){
            return null;
        }
        String extString = path.substring(path.lastIndexOf("."));
        extString = extString.toLowerCase();
        InputStream is;
        try{
            is = new FileInputStream(path);
            if (xls.equals(extString)) {
                wb = new HSSFWorkbook(is);
            } else if (xlsx.equals(extString)) {
                wb = new XSSFWorkbook(is);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return wb;
    }

    //取得輪帶表
    public int[][] getWheelArrays(Sheet sheet, int[][] wheelArrays){
        List<Integer> values;
        for(int i = 0; i < wheelArrays.length; i++){
            values = new ArrayList<>();
            for(Row r : sheet){
                Cell c = r.getCell(i);
                if(c != null){
                    if(c.getCellType() == CellType.NUMERIC){
                        values.add((int)c.getNumericCellValue());
                    }else if(c.getCellType() == CellType.FORMULA && c.getCachedFormulaResultType() == CellType.NUMERIC) {
                        values.add((int)c.getNumericCellValue());
                    }
                }
            }
            wheelArrays[i] = values.stream().mapToInt(x -> x).toArray();
        }
        return wheelArrays;
    }

    //取得輪帶表每軸總數
    public int[] getWheelsTotal(int[][] wheelArrays){
        int[] wheelsTotal = new int[wheelArrays.length];
        for(int i = 0; i < wheelArrays.length; i++){
            wheelsTotal[i] = wheelArrays[i].length;
        }
        return wheelsTotal;
    }

    //取得賠付表  icons:圖標有幾個, wheel:有幾軸
    public int[][] getPayTable(Sheet sheet, int icons, int wheel){
        int cellStartAt = 2;    //前兩格是編號及圖標名稱 故略過這兩格往後做輸入
        int[][] payTable = new int[icons][wheel];

        for(int i = 0; i < payTable.length; i++){
            Row row = sheet.getRow(i);
            for(int j = 0; j < payTable[i].length; j++){
                payTable[i][j] = (int)row.getCell(cellStartAt + j).getNumericCellValue();
            }
        }
        return payTable;
    }

    //取得設定參數
    public HashMap<String, Object> getParams(Sheet sheet){
        HashMap<String, Object> paramMap = new HashMap<>();

        Row row_Base = sheet.getRow(1);
        Row row_WheelsTotal = sheet.getRow(3);
        Row row_WheelsGrid = sheet.getRow(4);
        Row row_FreeSpinCount = sheet.getRow(7);

        //參數設置
        int wheel = (int)row_Base.getCell(1).getNumericCellValue();

        int startAt = 1;    //數值起始格
        int[] wheelsTotal = new int[wheel];     //每軸輪帶表長度
        int[] wheelsGrid = new int[wheel];      //本軸幾格
        int[] freeSpinCount = new int[wheel];   //幾個免費圖標送幾次免費轉數

        for(int i = 0; i < wheel; i++){
            wheelsTotal[i] = (int)row_WheelsTotal.getCell(startAt + i).getNumericCellValue();
            wheelsGrid[i] = (int)row_WheelsGrid.getCell(startAt + i).getNumericCellValue();
            freeSpinCount[i] = (int)row_FreeSpinCount.getCell(startAt + i).getNumericCellValue();
        }

        paramMap.put("Wheel", wheel);                                                             //幾軸
        paramMap.put("WildNum",          (int)row_Base.getCell(2).getNumericCellValue());       //Wild編號
        paramMap.put("FreeSpinNum",      (int)row_Base.getCell(3).getNumericCellValue());       //FreeSpin編號
        paramMap.put("LineBet",          (int)row_Base.getCell(4).getNumericCellValue());       //押注線數
        paramMap.put("IsWildToFreeSpin", (int)row_Base.getCell(5).getNumericCellValue() == 1);  //Wild可換FS
        paramMap.put("Icons",            (int)row_Base.getCell(6).getNumericCellValue());       //總共有幾個圖標
        paramMap.put("WheelsTotal", wheelsTotal);                                                 //每軸輪帶表長度
        paramMap.put("WheelsGrid", wheelsGrid);                                                   //本軸幾格
        paramMap.put("FreeSpinCount", freeSpinCount);                                             //幾個免費圖標送幾次免費轉數

        return paramMap;
    }

}
