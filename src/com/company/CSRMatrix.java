package com.company;

import java.math.BigDecimal;
import java.util.LinkedList;

class CSRMatrix {
    private static final String WHITESPACE = "\\s+";

    private class Data{
        BigDecimal data;
        int col;

        Data(BigDecimal data, int col){
            Change(data, col);
        }
        void Change(BigDecimal data, int col){
            this.data = data;
            this.col = col;
        }
    }
    private Data[] data;            //Data array
    private int[] row;              //Start column of each row
    private int colSize = 0;

    int GetNNZ(){
        return data == null ? 0 : data.length;
    }
    private int GetRowSize(){
        return row == null ? 0 : row.length;
    }
    private int GetColSize() { return colSize;}
    private BigDecimal GetEntry(int row, int col){
        int rowStart = this.row[row];
        int rowEnd = row == this.row.length - 1 ? data.length - 1 : (this.row[row + 1] - 1);

        for (int i = rowStart; i <= rowEnd; i++) {
            if (data[i].col == col){
                return data[i].data;
            } else if (data[i].col > col){
                return BigDecimal.ZERO;
            }
        }

        return BigDecimal.ZERO;
    }
    private int[] GetRow(){return row;}
    private Data[] GetData(){return data;}

    private void SetRow(int indx, int newRow){row[indx] = newRow;}
    void SetEntry(int row, int col, BigDecimal value){
        if (!GetEntry(row, col).equals(BigDecimal.ZERO)){
            if (!value.equals(BigDecimal.ZERO)){
                //We are fine...
                for (int i = this.row[row];
                     i < (row == this.row.length - 1 ? data.length - 1 : (this.row[row + 1] - 1));
                     i++) {
                    if (data[i].col == col){
                        data[i].data = value;
                        break;
                    }
                }
            } else{
                //Oh noooooo....
                //TODO: SET 0 FOR A NON ZERO ENTRY IN MATIX!!!
            }
        } else {
            if (!value.equals(BigDecimal.ZERO)){
                //Oh no...............
                //TODO: SET ENTRY FOR A ZERO ENTRY IN MATRIX!!!
            }
        }
    }
    private void SetData(int indx, BigDecimal value, int col){
        if (data[indx] == null){
            data[indx] = new Data(value, col);
        } else {
            data[indx].Change(value, col);
        }
    }

    CSRMatrix(String input){
        TakeInput(input);
    }
    private CSRMatrix(int rowSize, int colSize, int nnz){
        row = new int[rowSize];
        this.colSize = colSize;
        data = new Data[nnz];
    }
    private CSRMatrix(){};

    private void TakeInput(String input){
        boolean reallocate = false;     //Are we going to allocate new array?

        //Split the string into multiple regex
        String[] rows = input.split("\n");

        int rowSize = rows.length;

        if (rowSize <= 0){
            return;
        }

        colSize = rows[0].split(Main.WHITESPACE).length;
        if (row == null || rowSize != row.length){
            //Only create when take in different size
            row = new int[rowSize];
        }

        int nnz = 0;
        for(String row : rows){
            //Find the number of non zero
            for(String number: row.split(WHITESPACE)){
                if (number.equals("") || number.equals(WHITESPACE)){
                    continue;
                }
                number = number.replaceAll("\\s+","");
                if (!number.equals("0")){
                    nnz++;
                }
            }
        }

        if (data == null || nnz != data.length){
            data = new Data[nnz];
            reallocate = true;
        }

        int curRow = 0;
        int totalCol = 0;
        if (reallocate){
            while (curRow < rowSize){
                int curCol = 0;
                row[curRow] = totalCol;
                for(String number : rows[curRow++].split(WHITESPACE)){
                    if (number.equals("") || number.equals(WHITESPACE)){
                        continue;
                    }
                    number = number.replaceAll("\\s+","");
                    if (!number.equals("0")){
                        data[totalCol++] = new Data(BigDecimal.valueOf(Double.parseDouble(number)), curCol);
                    }
                    curCol++;
                }
            }
        } else {
            while (curRow < rowSize){
                int curCol = 0;
                row[curRow] = totalCol;
                for(String number : rows[curRow++].split(WHITESPACE)){
                    if (!number.equals("0")){
                        data[totalCol++].Change(BigDecimal.valueOf(Double.parseDouble(number)), curCol);
                    }
                    curCol++;
                }
            }
        }
    }
    void Print(){
        System.out.println("%Sparse matrix: ");
        for(int curRow = 0; curRow < row.length; curRow++){
            //Start and end index of the data array for the current row
            int rowStartIndx = row[curRow];
            //If we are at the last row, then the end index should be the length - 1
            int rowEndIndx = curRow == row.length - 1 ? data.length - 1 : (row[curRow + 1] - 1);

            //Magically (!!!), if the startIndex of the current row
            // is the same as the start index of the next row (which is the endIndex + 1), then
            // the endIndex - 1 < startIndex
            //This also indicates that the row is empty
            if (rowStartIndx > rowEndIndx){
                //Empty row, all 0
                for(int indx = 0; indx < colSize; indx++){
                    System.out.printf("%" + Main.MAX_SLOT + "." + Main.PRECISION + "f ", 0.0);
                }
            } else {
                int curIndx = rowStartIndx;
                for(int curCol = 0; curCol < colSize; curCol++){
                    if (curIndx < data.length && data[curIndx].col == curCol){
                        //If the current column matches the column of the non-zero entries we are looking at
                        //  then move on with the index and print out the corresponding data
                        System.out.printf("%" + Main.MAX_SLOT + "." + Main.PRECISION + "f ", data[curIndx].data);
                        if(curIndx < rowEndIndx){
                            curIndx++;
                        }
                    } else {
                        //If not then just print 0
                        System.out.printf("%" + Main.MAX_SLOT + "." + Main.PRECISION + "f ", 0.0);
                    }
                }
            }
            System.out.println();
        }
    }
    void PrintData(){
        if (data.length <= 0){
            System.out.println("%Matrix is empty!");
            return;
        }

        System.out.printf("%%%" + (Main.ENTRY_MAX_SLOT - 1) + "s " + "%" + Main.ENTRY_MAX_SLOT + "s " + "%" + Main.DATA_MAX_SLOT + "s", "Col", "Row", "Data");
        System.out.println();
        for(int curRow = 0; curRow < row.length; curRow++){
            for(int indx = row[curRow]; indx <= (curRow == row.length - 1 ? data.length - 1 : row[curRow + 1] - 1); indx++){
                System.out.printf("%" + Main.ENTRY_MAX_SLOT + "d %" + Main.ENTRY_MAX_SLOT + "d %" + Main.DATA_MAX_SLOT + "s"
                        , data[indx].col, curRow, data[indx].data);
                System.out.println();
            }
        }
    }

    Matrix GetMatrixForm(){
        //Return the CSR matrix in a normal form
        Matrix result = new Matrix(row.length, colSize);
        for(int curRow = 0; curRow < row.length; curRow++){
            //Start and end index of the data array for the current row
            int rowStartIndx = row[curRow];
            //If we are at the last row, then the end index should be the length - 1
            int rowEndIndx = curRow == row.length - 1 ? data.length - 1 : (row[curRow + 1] - 1);

            //Magically (!!!), if the startIndex of the current row
            // is the same as the start index of the next row (which is the endIndex + 1), then
            // the endIndex - 1 < startIndex
            //This also indicates that the row is empty
            if (rowStartIndx <= rowEndIndx){
                int curIndx = rowStartIndx;
                for(; curIndx <= rowEndIndx; curIndx++){
                    result.SetEntry(curRow, data[curIndx].col, data[curIndx].data);
                }
            }
        }
        return result;
    }
    CSRMatrix GetTranspose(){
        CSRMatrix result = new CSRMatrix(colSize, row.length, data.length);

        //Record the index of the data that we are taking a look at in each row
        int[] rowCurCol = new int[row.length];
        for(int curRow = 0; curRow < row.length - 1; curRow++){
            //Either the row is empty or has something in it
            rowCurCol[curRow] = row[curRow] == row[curRow + 1] ?  -1 : row[curRow];
        }
        //Is last row empty?
        rowCurCol[row.length - 1] = row[row.length - 1] == data.length ? -1 : row[row.length - 1];

        //Every entry with the same col in all rows in A should be in the same row in A^T
        int curRow = 0;
        int newTotalCol = 0;
        for(int curCol = 0; curCol < colSize; curCol++){
            result.SetRow(curRow++, newTotalCol);
            int newCol = 0;
            for(int row = 0; row < this.row.length; row++){
                if (rowCurCol[row] != -1){
                    //Not empty or still has something on that row
                    if (data[rowCurCol[row]].col == curCol){
                        //We are taking a look at the same column!!!
                        //  Add to the new list and move on!
                        result.SetData(newTotalCol++, data[rowCurCol[row]].data, newCol++);

                        //Either we are at the end of the list or we are at the end of the row
                        rowCurCol[row] = (row >= this.row.length - 1 && rowCurCol[row] + 1 == data.length)
                                //At the end of the row
                                || (row < this.row.length - 1 && rowCurCol[row] + 1 == this.row[row + 1])
                                //Disable or move on to the next index
                                ? -1 : rowCurCol[row] + 1;
                    }
                }
            }
        }

        return result;
    }
    Vector TimeVector(Vector parm){
        Vector result = new Vector(row.length, BigDecimal.ZERO);

        for(int curRow = 0; curRow < row.length; curRow++){
            BigDecimal temp = BigDecimal.ZERO;
            for(int indx = row[curRow]; indx <= (curRow == row.length - 1 ? data.length - 1 : (row[curRow + 1] - 1)); indx++){
                temp = temp.add(data[indx].data.multiply(parm.GetEntry(data[indx].col), Main.mathContext), Main.mathContext);
            }
            result.SetEntry(curRow, temp);
        }
        return result;
    }
    CSRMatrix TimeMatrix(CSRMatrix matrix){
        if (matrix.GetRowSize() != colSize){
            return null;
        }
        int[] rowCurCol = new int[colSize];
        int[] parmRow = matrix.GetRow();
        Data[] parmData = matrix.GetData();
        int[] resultRow = new int[row.length];  //This will be used as the row arr of the result matrix

        //rowCurCol is used to keep track of the current col that we are reading
        for(int curRow = 0; curRow < parmRow.length - 1; curRow++){
            //Either the row is empty (-1) or has something in it (which we will store the start index)
            rowCurCol[curRow] = parmRow[curRow] == parmRow[curRow + 1] ?  -1 : parmRow[curRow];
        }
        //Is last row empty?
        rowCurCol[parmRow.length - 1] = parmRow[parmRow.length - 1] == parmData.length ? -1 : parmRow[parmRow.length - 1];

        for(int curRow = 0; curRow < row.length; curRow++){
            resultRow[curRow] = 0;
        }

        int nnz = 0;

        //Since accessing row-wise is much easier, we will put it in the inner loop
        for(int parmCol = 0; parmCol < matrix.GetColSize(); parmCol++){
            for(int curRow = 0; curRow < row.length; curRow++){
                BigDecimal temp = BigDecimal.ZERO;
                //k will go from the index of the first element in the current row to the end index
                //  then data[k].col will be used as the row of parmData and
                // we will use rowCurCol[data[k].col] to look aside the current element in parm
                //Moreover, because we use the outer loop to iterate the parmCol,
                // if parmCol != parmData[rowCurCol[data[k].col]].col
                //  then parm[data[k].col][parmCol] == 0
                for(int k = row[curRow];
                    k <= (curRow == row.length - 1 ? data.length - 1 : row[curRow + 1] - 1);
                    k++){
                    //Only multiply non zero!
                    if (rowCurCol[data[k].col] > -1 && parmData[rowCurCol[data[k].col]].col == parmCol){
                        //Since we iterate the parmCol in the outer loop one by one,
                        //  if the col of the rowCurCol doesn't match the outer loop,
                        //  it means the parm[parmData[rowCurCol[data[k].col]].col][parmCol] == 0
                        //Thus, we don't need to include it in our calculation
                        temp = temp.add(data[k].data.multiply(parmData[rowCurCol[data[k].col]].data, Main.mathContext), Main.mathContext);
                    }
                }
                if (!temp.equals(BigDecimal.ZERO)){
                    nnz++;
                    //And update the row array of the result matrix
                    for(int indx = curRow + 1; indx < row.length; indx++){
                        resultRow[indx]++;
                    }
                }
            }

            //Increase the curCol
            for(int indx = 0; indx < rowCurCol.length; indx++){
                if (rowCurCol[indx] > -1 && parmCol == parmData[rowCurCol[indx]].col){
                    if ((indx >= parmRow.length - 1 && rowCurCol[indx] + 1 == parmData.length)
                            || (indx < parmRow.length - 1 && rowCurCol[indx] + 1 == parmRow[indx + 1])){
                        rowCurCol[indx] = -1;
                    } else {
                        rowCurCol[indx]++;
                    }
                }
            }
        }

        //CSRMatrix result = new CSRMatrix(row.length, matrix.GetColSize(),nnz);
        CSRMatrix result = new CSRMatrix();
        result.row = resultRow;
        result.colSize = matrix.GetColSize();
        result.data = new Data[nnz];

        //Reset!
        for(int curRow = 0; curRow < parmRow.length - 1; curRow++){
            //Either the row is empty (-1) or has something in it (which we will store the start index)
            rowCurCol[curRow] = parmRow[curRow] == parmRow[curRow + 1] ?  -1 : parmRow[curRow];
        }
        //Is last row empty?
        rowCurCol[parmRow.length - 1] = parmRow[parmRow.length - 1] == parmData.length ? -1 : parmRow[parmRow.length - 1];

        //Since accessing row-wise is much easier, we will put it in the inner loop
        for(int parmCol = 0; parmCol < matrix.GetColSize(); parmCol++){
            for(int curRow = 0; curRow < row.length; curRow++){
                BigDecimal temp = BigDecimal.ZERO;
                //k will go from the index of the first element in the current row to the end index
                //  then data[k].col will be used as the row of parmData and
                // we will use rowCurCol[data[k].col] to look aside the current element in parm
                //Moreover, because we use the outer loop to iterate the parmCol,
                // if parmCol != parmData[rowCurCol[data[k].col]].col
                //  then parm[data[k].col][parmCol] == 0
                for(int k = row[curRow];
                    k <= (curRow == row.length - 1 ? data.length - 1 : row[curRow + 1] - 1);
                    k++){
                    //Only multiply non zero!
                    if (rowCurCol[data[k].col] > -1 && parmData[rowCurCol[data[k].col]].col == parmCol){
                        //Since we iterate the parmCol in the outer loop one by one,
                        //  if the col of the rowCurCol doesn't match the outer loop,
                        //  it means the parm[parmData[rowCurCol[data[k].col]].col][parmCol] == 0
                        //Thus, we don't need to include it in our calculation
                        temp = temp.add(data[k].data.multiply(parmData[rowCurCol[data[k].col]].data, Main.mathContext), Main.mathContext);
                    }
                }

                if (!temp.equals(BigDecimal.ZERO)){
                    //Since we go from left to right (low parmCol to high parmCol)
                    //  we only need to find a spot where it is empty to put the new entry in
                    for(int k = resultRow[curRow];
                        k <= (curRow == resultRow.length - 1 ? result.data.length - 1 : resultRow[curRow + 1] - 1);
                        k++){
                        if (result.data[k] == null){
                            result.data[k] = new Data(temp, parmCol);
                            break;
                        }
                    }
                }
            }

            //Increase the curCol
            for(int indx = 0; indx < rowCurCol.length; indx++){
                if (rowCurCol[indx] > -1 && parmCol == parmData[rowCurCol[indx]].col){
                    if ((indx >= parmRow.length - 1 && rowCurCol[indx] + 1 == parmData.length)
                            || (indx < parmRow.length - 1 && rowCurCol[indx] + 1 == parmRow[indx + 1])){
                        rowCurCol[indx] = -1;
                    } else {
                        rowCurCol[indx]++;
                    }
                }
            }
        }

        return result;
    }
    CSRMatrix TimeMatrix(Matrix matrix){
        if (colSize != matrix.GetRowSize()){
            return null;
        }

        Vector[] parmData = matrix.GetMatrix();

        int nnz = 0;
        for(int curRow = 0; curRow < row.length; curRow++){
            for(int curCol = 0; curCol < matrix.GetColSize(); curCol++){
                BigDecimal temp = BigDecimal.ZERO;
                for(int indx = row[curRow]; indx <= (curRow == row.length - 1 ? data.length - 1 : (row[curRow + 1] - 1)); indx++){
                    temp = temp.add(data[indx].data.multiply(parmData[data[indx].col].GetEntry(curCol)), Main.mathContext);
                }
                if (!temp.equals(BigDecimal.ZERO)){
                    nnz++;
                }
            }
        }

        CSRMatrix result = new CSRMatrix(row.length, matrix.GetColSize(), nnz);

        int curDataIndx = 0;
        for(int curRow = 0; curRow < row.length; curRow++){
            result.row[curRow] = curDataIndx;
            for(int curCol = 0; curCol < matrix.GetColSize(); curCol++){
                BigDecimal temp = BigDecimal.ZERO;
                for(int indx = row[curRow]; indx <= (curRow == row.length - 1 ? data.length - 1 : (row[curRow + 1] - 1)); indx++){
                    temp = temp.add(data[indx].data.multiply(parmData[data[indx].col].GetEntry(curCol)), Main.mathContext);
                }
                if (!temp.equals(BigDecimal.ZERO)){
                    result.data[curDataIndx++] = new Data(temp, curCol);
                }
            }
        }

        return result;
    }

    Vector IterationMethod(Vector rightSide){
        final int STEP_LIMIT = 100;
        final double TOLERANCE = 10e-6;
        final int MAX_SEARCH_DIR = 50;

        LinkedList<Vector> searchDirList = new LinkedList<>();
        int searchDirIndx = 0;                                                      //Is it time to restart the search dir yet?
        //Vector solution = new Vector(rightSide);    //Create an initial vector
        Vector solution = new Vector(rightSide.GetSize(), BigDecimal.ZERO);
        Vector residual = rightSide.Add(this.TimeVector(solution.Scale(BigDecimal.valueOf(-1))));       //Calculate the first residue
        BigDecimal norm = residual.GetLength();                                         //Calculate the norm
        BigDecimal initialNorm = norm;
        Vector curSearchDir;
        searchDirList.add(curSearchDir = new Vector(residual.Normalize()));                        //Calculate the initial search direction
        Matrix pMatrix = new Matrix(curSearchDir, 2);
        Matrix bMatrix = new Matrix(this.TimeVector(curSearchDir), 2);

        for(int step = 1; step < STEP_LIMIT && norm.compareTo(initialNorm.multiply(BigDecimal.valueOf(TOLERANCE), Main.mathContext)) > 0; step++){
            //|r - bMatrix * alpha| -> min
            //<=> least square(r = bMatrix * alpha)
            //<=> least square(r = QR * alpha)
            //<=> least square(Q^T r = R alpha)
            //<=> beta = R * alpha => least square!
            LinkedList<Matrix> qrFactor = bMatrix.QRFactorization();
            if (qrFactor.size() < 2){
                System.out.println("Can't do QR Factorization!!!");
                return null;
            }

            //Calculate alpha
            Vector alpha = qrFactor.get(1).BackwardSubstitution(qrFactor.get(0).GetTranspose().TimeVector(residual));
            //Calculate next iterate
            solution = solution.Add(pMatrix.TimeVector(alpha));
            //Calculate next residual
            residual = residual.Add(bMatrix.TimeVector(alpha).Scale(BigDecimal.valueOf(-1)));
            //Calculate residual norm
            norm = residual.GetLength();

            //Calculate next search dir less than max, or else move back to the first one
            if (searchDirIndx  < MAX_SEARCH_DIR){
                searchDirList.add((curSearchDir = new Vector(residual)));
                searchDirIndx++;

                //r - Sigma (k = 1->searchDirIndx)((r,p(k)) * p(k))
                for(int k = 0; k < searchDirIndx; k++){
                    Vector preSearchDir = searchDirList.get(k);
                    //+ (- (r,p(k)) * p(k))
                    curSearchDir = curSearchDir.Add(preSearchDir.Scale(preSearchDir.InnerProduct(residual)).Scale(BigDecimal.valueOf(-1)));
                }
                curSearchDir = curSearchDir.Normalize();

                pMatrix = pMatrix.AugmentVectorAtEnd(curSearchDir);
                bMatrix = bMatrix.AugmentVectorAtEnd(this.TimeVector(curSearchDir));
            } else {
                //Restart
                searchDirIndx = 0;
                curSearchDir = searchDirList.get(searchDirIndx);
                curSearchDir.Copy(residual.Normalize());

                pMatrix = new Matrix(curSearchDir, 2);
                bMatrix = new Matrix(this.TimeVector(curSearchDir), 2);
            }
        }

        return solution;
    }

    static boolean IsSymmetric(CSRMatrix parm){
        if (parm.GetRowSize() != parm.GetColSize()){
            //Not a square matrix!
            return false;
        }

        int[] row = parm.GetRow();
        Data[] data = parm.GetData();

        for(int curRow = 0; curRow < parm.GetRowSize(); curRow++){
            //We only need to check until the middle entries
            for(int indx = row[curRow];
                indx <= (curRow == row.length - 1 ? data.length - 1 : (row[curRow + 1] - 1)) && data[indx].col < curRow;
                indx++){
                //Find the correct entry
                for(int search = row[data[indx].col];
                    search <= (data[indx].col == row.length - 1 ? data.length - 1 : (row[data[indx].col + 1] - 1));
                    search++
                ){
                    if (data[search].col > curRow || (data[search].col == curRow && !data[indx].data.equals(data[search].data))){
                        //Different data
                        return false;
                    }
                }

            }
        }
        return true;
    }
}
