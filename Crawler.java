import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *Reads and searches through the selected links
 * @author Tsoum
 */
public class Crawler {
  private String[] type;    // periexei tis lexeis "sigmod","pods","icde","edbt";
  private String[] confURL; /* ta links twn syndriwn */
  private String[] years;  /* periexei 2002.html klp */
   
  private Object[] keys;
  private Object[] values;  
  
  private String[] contents;
  private int numberOfContents;
  
  public Crawler(String[] type, String[] confURL, String[] years) {
      this.confURL=confURL;
      this.type=type;
      this.years=years;
  }
  
  /* Diavazeo mia selida apo to internet kai tin apothikevei se ena string */
  public String getPage(String url) throws MalformedURLException, IOException {
      URL conferenecePage;
      URLConnection uc;
      String wholeFile="";
    
      conferenecePage= new URL(url);
      uc= conferenecePage.openConnection();      
      
      Scanner input= new Scanner(uc.getInputStream());
     
      while( input.hasNextLine() ) {
          wholeFile=wholeFile+ input.nextLine();
      }
      input.close();    
      
      return wholeFile;
  }
  
  /* dimiourgei enan pinaka o opoios periexei ta links twn artrhwn gia ta synedria kai ta eti poy exei epilexei o xristis */
  private void collectContents() throws MalformedURLException, IOException 
  {            
      contents=new String[type.length * years.length];
      numberOfContents=0;    
      
      for (int i=0; i<type.length; i++) {
        if (type[i].equals("")) continue;
        String wholeFile=getPage(confURL[i]);
        String[] years_CNFs=new String[years.length];
        for (int j=0; j<this.years.length; j++) {
            if (years[j].equals("")) years_CNFs[j]="";
            else years_CNFs[j] = type[i]+years[j];
        }    
        
        for(int j=0; j<12; ++j) {
          if( !years_CNFs[j].equals("") )
          {
            int pos= wholeFile.indexOf( years_CNFs[j] );
            if (pos>=0) {
                int begin=  wholeFile.lastIndexOf("a href=", pos);
                if (begin>=0) {
                    contents[numberOfContents]= wholeFile.substring(begin+8, pos+years_CNFs[j].length());
                    numberOfContents++;
                }
            }
          }
        }
      }
  }
  
  /* metraei tis lexeis kai tis emfaniseis gia ola ta artra poy exoyn epilxthei */
  public void collectTerms() throws MalformedURLException, IOException
  {  
      collectContents();
      
      HashMap<String, Integer> htable= new HashMap<String, Integer>();
      int numPairs;
      
      for (int i=0; i<numberOfContents; i++) {  
        String wholeFile=getPage(contents[i]);
        int aTitleStarts= -1;
        int searchFromHere= wholeFile.indexOf("ISBN", 0);   //Skip the name of the conference; it is not an article title.
        String delimiters = " ,-.\"\\\n\t+:'?()";
        while( (aTitleStarts=wholeFile.indexOf("<span class=\"title\">", searchFromHere)) != -1 )
        {      
          int aTitleEnd= wholeFile.indexOf("</span>", aTitleStarts);
          String articleTitle= wholeFile.substring(aTitleStarts+20, aTitleEnd);
          searchFromHere= aTitleEnd;
          StringTokenizer st= new StringTokenizer(articleTitle, delimiters);
          while( st.hasMoreTokens() )
          {
            String token= st.nextToken();
            token= token.toLowerCase();
            if( htable.containsKey(token) )
            {
              int count= htable.get( token );  /* afxanei kata ena ton arithmo emfanisewn tou token */
              htable.put(token, count+1);
            }
            else
              htable.put(token, new Integer(1)); /* vazei ena kainourio token ston hash tabel */
          }
        }
    }    
        
    numPairs= htable.size();
    keys= new String[numPairs];
    values= new Integer[numPairs];
    int i= 0;
    
    Iterator it= htable.entrySet().iterator();
    while( it.hasNext() )
    {
      Map.Entry pairs = (Map.Entry)it.next();
      //System.out.println( pairs.getKey() + " = " + pairs.getValue() );
      keys[i]= pairs.getKey();
      values[i]= pairs.getValue();
      ++i;
      it.remove();                      //Avoids a ConcurrentModificationException
    }

    //Bubble-sort of the arrays based on the values.
    String swapString= "";
    Integer swapInt= 0;
    for(int c=0; c<(numPairs-1); c++)
    {
      for(int d=0; d<numPairs-c-1; d++)
      {
        if( (int)getValues()[d] < (int)getValues()[d+1] )    //Descending order
        {
          swapInt= (Integer)getValues()[d];  swapString= (String)getKeys()[d];
          values[d]= getValues()[d+1];       keys[d]= getKeys()[d+1];
          values[d+1]= swapInt;              keys[d+1]= swapString;
        }
      }
    }
  }
  
  
  public void save(String filename) throws FileNotFoundException {
      PrintWriter file=new PrintWriter(new FileOutputStream(filename));
      
      int numPairs=getKeys().length;
      if (numPairs==0) return;
      
      String[] aKeys=new String[numPairs];
      Integer[] aValues=new Integer[numPairs];
      for(int i=0; i<numPairs; i++) { 
          aKeys[i]=(String) keys[i];
          aValues[i]=(Integer) values[i];
      }
      /*alfavitiki taksinomisi*/
      String swapString= "";
      Integer swapInt= 0;
      for(int c=0; c<(numPairs-1); c++)
      {
        for(int d=0; d<numPairs-c-1; d++)
        {
            if (aKeys[d].compareTo(aKeys[d+1])>0)   //Ascending order
            {
              swapInt= aValues[d];          swapString= aKeys[d];
              aValues[d]= aValues[d+1];     aKeys[d]= aKeys[d+1];
              aValues[d+1]= swapInt;        aKeys[d+1]= swapString;
            }
        }
      }
      
      /* save to file */
      for (int i=0; i<numPairs; i++) {
          file.println(aKeys[i]+","+aValues[i]);
      }
      
      file.close();
  } 

    /**
     * @return the keys
     */
    public Object[] getKeys() {
        return keys;
    }

    /**
     * @return the values
     */
    public Object[] getValues() {
        return values;
    }
}
