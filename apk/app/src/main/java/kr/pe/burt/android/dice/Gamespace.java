package kr.pe.burt.android.dice;

import java.util.Random;
import java.lang.Math;

public class Gamespace {


        public Boolean modyfied;
        public int rastret = -5;
        public int gamemode;
        public int score;
        public int winrate;
        public int winner;
        public int  matrix[][][] = new int[4][4][4];
        public Boolean turntest(){
            //int lim = maxsize-1;
            int currient;
            int rt = 0;
            int tr1 = 1;
            for(int x=0 ;x<matrix.length ; x++){
                for(int y=0; y<matrix[0].length; y++){
                    for(int z=0; z<matrix[0][0].length; z++){
                        currient = matrix[x][y][z];
                        if(currient == matrix[x][y][z+1] && z+1<matrix[0][0].length || currient == matrix[x][y+1][z] && y+1<matrix[0].length || currient == matrix[x+1][y][z] && x+1<matrix.length  )return( Boolean.TRUE);
                    }
                }
            }
            return (Boolean.FALSE);
        }



        public int step( int dir, int pos){
            int ret = 0;
            int delta;
            if(dir%2 != 0) delta=-1;
            else delta=1;
            switch(dir/2){
                case 0:

                    for(int y=0; y<matrix[0].length; y++){
                        for(int z=0; z<matrix[0][0].length; z++){
                            if(matrix[pos][y][z]!=0){
                                if(matrix[pos+delta][y][z]==0){
                                    matrix[pos+delta][y][z]=matrix[pos][y][z];
                                    matrix[pos][y][z]=0;
                                    ret++;               }
                                else if(matrix[pos+delta][y][z]==matrix[pos][y][z]&&matrix[pos][y][z]>0){
                                    matrix[pos+delta][y][z]++;
                                    score += 1<<matrix[pos+delta][y][z];
                                    matrix[pos+delta][y][z]*=-1;
                                    matrix[pos][y][z]=0;
                                    ret++;
                                }
                            }
                        }
                    }
                    break;
                case 1:

                    for(int x=0; x<matrix.length; x++){
                        for(int z=0; z<matrix[0][0].length; z++){
                            if(matrix[x][pos][z]!=0){
                                if(matrix[x][pos+delta][z]==0){
                                    matrix[x][pos+delta][z]=matrix[x][pos][z];
                                    matrix[x][pos][z]=0;
                                    ret++;               }
                                else if(matrix[x][pos+delta][z]==matrix[x][pos][z]&&matrix[x][pos][z]>0){
                                    matrix[x][pos+delta][z]++;
                                    score += Math.pow(2, matrix[x][pos+delta][z]);
                                    matrix[x][pos+delta][z]*=-1;
                                    matrix[x][pos][z]=0;
                                    ret++;
                                }
                            }
                        }
                    }
                    break;
                case 2:

                    for(int x=0; x<matrix.length; x++){
                        for(int y=0; y<matrix[0].length; y++){
                            if(matrix[x][y][pos]!=0){
                                if(matrix[x][y][pos+delta]==0){
                                    matrix[x][y][pos+delta]=matrix[x][y][pos];
                                    matrix[x][y][pos]=0;
                                    ret++;               }
                                else if(matrix[x][y][pos+delta]==matrix[x][y][pos]&&matrix[x][y][pos]>0){
                                    matrix[x][y][pos+delta]++;
                                    score += Math.pow(2, matrix[x][y][pos+delta]);
                                    matrix[x][y][pos+delta]*=-1;
                                    matrix[x][y][pos]=0;
                                    ret++;

                                }
                            }
                        }
                    }
                    break;


            }
            return(ret);


        }


        public int turn(int dir){
    /*    if(modyfied ){
            memcpy(&bufer, &inuse, sizeof(gamespace));
        }


     */     if(gamemode!=1) newgame();



            int ret =0;
            int lastret=-1;
            if(dir%2!=0)
                while (ret-lastret!=0){
                    {
                        lastret=ret;
                        for(int i=1; i<matrix[0][0].length; i++)
                            ret+=step(dir,i);
                    }
                }
            else
                while (ret-lastret!=0){
                    {
                        lastret=ret;
                        for(int i=matrix[0][0].length-2; i>=0; i--)
                            ret+=step(dir,i);
                    }

                }
            int space =0 ;
            for(int x=0 ;x<matrix.length ; x++){
                for(int y=0; y<matrix[0].length; y++){
                    for(int z=0; z<matrix[0][0].length; z++){
                        matrix[x][y][z]=Math.abs(matrix[x][y][z]);
                        if( matrix[x][y][z]>=winrate && winner!=0){
                            gamemode = 2;
                            winner = 1;
                        }
                        else if(matrix[x][y][z]==0) space++;
                    }
                }
            }

            modyfied = ret!=0;
            if(modyfied){
                //  memcpy(&back, &bufer, sizeof(gamespace));

                if (space==0 ) gamemode = 3;
                else
                    newcube();
            }
            else{
                if(!turntest() && space==0){
                    gamemode = 3;
                }
            }


            return(ret);
        }

        public void newgame(){
            gamemode = 1;
            winner = 0;
            for(int x=0 ;x<matrix.length ; x++){
                for(int y=0; y<matrix[0].length; y++){
                    for(int z=0; z<matrix[0][0].length; z++){
                        matrix[x][y][z]=0;
                    }
                }
            }
            newcube();
            newcube();
            //rotate_x=0;
            //rotate_y=0;
            score = 0;
            //memcpy(&back, &inuse,sizeof(gamespace));
            //memcpy(&bufer, &inuse,sizeof(gamespace));
            //mise_x = 0;
            //mise_y = 0;
            modyfied=Boolean.TRUE;
            //radius = 2;

        }

        public static int randInt(int min, int max) {

            // NOTE: This will (intentionally) not run as written so that folks
            // copy-pasting have to think about how to initialize their
            // Random instance.  Initialization of the Random instance is outside
            // the main scope of the question, but some decent options are to have
            // a field that is initialized once and then re-used as needed or to
            // use ThreadLocalRandom (if using at least Java 1.7).
            //
            // In particular, do NOT do 'Random rand = new Random()' here or you
            // will get not very good / not very random results.
            Random rand=new Random();

            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            int randomNum = rand.nextInt((max - min) + 1) + min;

            return randomNum;
        }


        void newcube(){
            int x,y,z;
            do{
                x = randInt(0,matrix.length-1);
                y = randInt(0,matrix[0].length-1);
                z = randInt(0,matrix[0][0].length-1);
            }while(matrix[x][y][z]!=0);
            matrix[x][y][z]=1;

        }





    }


