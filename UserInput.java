import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * Creates the GUI
 * @author Tsoum
 */
public class UserInput extends JFrame implements ActionListener
{
  public static final int WIDTH= 500;
  public static final int HEIGHT= 200;
  
  private static final String SIGMOD="http://www.informatik.uni-trier.de/~ley/db/conf/sigmod/index.html";
  private static final String PODS="http://www.informatik.uni-trier.de/~ley/db/conf/pods/index.html";
  private static final String ICDE="http://www.informatik.uni-trier.de/~ley/db/conf/icde/index.html"; 
  private static final String EDBT="http://www.informatik.uni-trier.de/~ley/db/conf/edbt/index.html"; 
  
  private static final String[] APPS = {"0","2","4","6","8","10","12","14","16","18","20","22","24","26","28","30"};
  private static final String[] TOPSEL = {"top-1","top-5","top-10","top-20","top-50","top-100","All"};
  private static final int[] TOPVAL = {1,5,10,20,50,100,0};
  
  private String URL2beDownloaded= "";
  private JMenu menu2;
  
  private String sigmodURL="";
  private String podsURL="";
  private String icdeURL="";
  private String edbtURL="";
  
  private String[] years=new String[12];
  
  private String excl="";
  private int appearance;
  private int top;
  private Crawler crawler;
  
  @Override
  public void actionPerformed(ActionEvent e) {
      String menuItemActivated = e.getActionCommand();

      if (menuItemActivated.equals("Run")) {
         menu_run(); 
      } else
      if (menuItemActivated.equals("Save")) {
         menu_save(); 
      } else
      if (menuItemActivated.equals("Conferences")) {
         menu_conferences();
      } else 
      if (menuItemActivated.equals("Years")) {
          menu_years();
      } else
      if (menuItemActivated.equals("Options")) {
          menu_options();
      } else
      if (menuItemActivated.equals("BarCharts")) {
          menu_barcharts();
      }
  }
  
  /* epilogi anazitisis*/
  private void menu_run() {
      String[] type=new String[]{"sigmod","pods","icde","edbt"};
      String[] confURL=new String[4];
      
      if (sigmodURL.equals("")) {
          type[0]="";
      }
      if (podsURL.equals("")) {
          type[1]="";
      } 
      if (icdeURL.equals("")) {
          type[2]="";
      }
      if (edbtURL.equals("")) {
          type[3]="";
      }   
      
      confURL[0]=sigmodURL;
      confURL[1]=podsURL;
      confURL[2]=icdeURL;
      confURL[3]=edbtURL;
      try {
            crawler=new Crawler(type, confURL, years);
            crawler.collectTerms();
            JOptionPane.showMessageDialog(this, "Calculation completed!", "Crawler message", JOptionPane.INFORMATION_MESSAGE);
      } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(this, "URL is wrong!", "Crawler message", JOptionPane.ERROR_MESSAGE);
      } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "IO error occurred", "Crawler message", JOptionPane.ERROR_MESSAGE);
      }
  }
  
  private void menu_save() {
      if (crawler==null) {
           JOptionPane.showMessageDialog(this, "No results to save", "Crawler message", JOptionPane.ERROR_MESSAGE);
           return;
      }      
      JFileChooser chooser=new JFileChooser();
      if (chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
        String filename=chooser.getSelectedFile().getAbsolutePath();
        try {
            crawler.save(filename);
        } catch (FileNotFoundException ex) {
           JOptionPane.showMessageDialog(this, "Unable to create file", "Crawler message", JOptionPane.ERROR_MESSAGE);
        }
      }
  }
  
  private void menu_conferences() {
     Conferences fr=new Conferences(this); 
  }
  
  private void menu_years() {
     Years yr=new Years(this);
  }

  private void menu_options() {
      KritiriaEmfanisis ke=new KritiriaEmfanisis(this);
  }
  
  private void menu_barcharts() {
      if (crawler==null) {
           JOptionPane.showMessageDialog(this, "No results to show", "Crawler message", JOptionPane.ERROR_MESSAGE);
           return;
      }
      Object[] keys=crawler.getKeys();
      Object[] values=crawler.getValues();
      
      ArrayList<String> words=new ArrayList<String>();
      ArrayList<Integer> counters=new ArrayList<Integer>();
      ArrayList<String> exclusions=new ArrayList<String>();     
          
      StringTokenizer st= new StringTokenizer(excl, " \n\t.,");          
      while (st.hasMoreTokens())
      {
            String token= st.nextToken();
            token= token.toLowerCase();
            exclusions.add(token);
      }
      
      int numOfWords=0;
      for(int i=0; i<keys.length && (top==0 || numOfWords<top); i++) {
          String word=(String) keys[i];
          int counter=(Integer) values[i];
          if (! exclusions.contains(word) && counter>appearance) {
              words.add(word);
              counters.add(counter);
              numOfWords++;
          }
      }
      
      if (words.size()>0) {
          new Barcharts(words, counters);
      } else {
         JOptionPane.showMessageDialog(this, "Nothing to display", "Crawler message", JOptionPane.ERROR_MESSAGE);          
      }
  }
  /**
   * Creates windows
   */
  public UserInput() {
      super("JAVA Project");
      this.setSize(WIDTH, HEIGHT);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.getContentPane().setBackground(Color.DARK_GRAY);
      
      /* arxikopoihsh */
      for (int i=0; i<years.length; i++) {
          years[i]="";
      }

      /* xtisimo menu */
      JMenuBar mBar= new JMenuBar();
      setJMenuBar( mBar );
      
      JMenu menu1 = new JMenu( "File" );
      menu2= new JMenu( "Select" );
      JMenu menu3 = new JMenu( "View" );

      mBar.add(menu1);
      mBar.add(menu2);
      mBar.add(menu3);
      
      JMenuItem mitemRun = new JMenuItem( "Run" );
      JMenuItem mitemSave = new JMenuItem( "Save" );
      JMenuItem mitemConferences = new JMenuItem( "Conferences" );
      JMenuItem mitemYears = new JMenuItem( "Years" );
      JMenuItem mitemOptions = new JMenuItem( "Options" );
      JMenuItem mitemBarCharts = new JMenuItem( "BarCharts" );
      menu1.add(mitemRun);
      menu1.add(mitemSave);
      menu2.add(mitemConferences);
      menu2.add(mitemYears);
      menu3.add(mitemOptions);
      menu3.add(mitemBarCharts);
      
      mitemRun.addActionListener(this);
      mitemSave.addActionListener(this);
      mitemConferences.addActionListener(this);
      mitemYears.addActionListener(this);
      mitemOptions.addActionListener(this);
      mitemBarCharts.addActionListener(this);
  }

  
  public static void main( String[] args )
  {
    UserInput dl= new UserInput();
    dl.setVisible( true );
  }

  private class Conferences extends JDialog implements ItemListener
  {     
    private JCheckBox sigmod=new JCheckBox("SIGMOD");
    private JCheckBox pods=new JCheckBox("PODS");
    private JCheckBox icde=new JCheckBox("ICDE");
    private JCheckBox edbt=new JCheckBox("EDBT");
    
    public Conferences(Frame aFrame)
    {
      super(aFrame, "Conferences", true);
      setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
      setSize(3*aFrame.getWidth()/4, aFrame.getHeight()/2);
      setResizable( false );
      
      JPanel p1=new JPanel();
      JPanel p2=new JPanel();
      this.setLayout(new BorderLayout());
      p1.setLayout( new GridLayout(2, 2) );
      p2.setLayout(new FlowLayout());
      add(p1,BorderLayout.CENTER);
      add(p2,BorderLayout.SOUTH);
      
      p1.add(sigmod);
      p1.add(pods);
      p1.add(icde);
      p1.add(edbt);
    
      if (! sigmodURL.equals("")) sigmod.setSelected(true);
      if (! podsURL.equals("")) pods.setSelected(true);
      if (! icdeURL.equals("")) icde.setSelected(true);
      if (! edbtURL.equals("")) edbt.setSelected(true);
            
      sigmod.addItemListener(this);
      pods.addItemListener(this);
      icde.addItemListener(this);
      edbt.addItemListener(this);

      JButton OK_but= new JButton( "OK" );
      p2.add( OK_but );
      setLocationRelativeTo( aFrame );
      
      OK_but.addActionListener(new ActionListener() {

        @Override
            public void actionPerformed(ActionEvent e) {   
            dispose();
              }
          });    
      setVisible( true );
    }

        @Override
        public void itemStateChanged(ItemEvent e) {
            Object source= e.getItemSelectable();
            if( source == sigmod ) {
                if (sigmod.getSelectedObjects()!=null) sigmodURL=SIGMOD;
                else sigmodURL="";
            }
            if( source == pods ) {
                if (pods.getSelectedObjects()!=null) podsURL=PODS;
                else podsURL="";
        }
            if( source == icde ) {
                if (icde.getSelectedObjects()!=null) icdeURL=ICDE;
                else icdeURL="";
            }
            
            if( source == edbt ) {
                if (edbt.getSelectedObjects()!=null) edbtURL=EDBT;
                else edbtURL="";
            }
        }
        
  }
            
  private class Years extends JDialog implements ItemListener
  {    
    private JCheckBox YEAR2002=new JCheckBox("2002");
    private JCheckBox YEAR2003=new JCheckBox("2003");
    private JCheckBox YEAR2004=new JCheckBox("2004");
    private JCheckBox YEAR2005=new JCheckBox("2005");
    private JCheckBox YEAR2006=new JCheckBox("2006");
    private JCheckBox YEAR2007=new JCheckBox("2007");
    private JCheckBox YEAR2008=new JCheckBox("2008");
    private JCheckBox YEAR2009=new JCheckBox("2009");
    private JCheckBox YEAR2010=new JCheckBox("2010");
    private JCheckBox YEAR2011=new JCheckBox("2011");
    private JCheckBox YEAR2012=new JCheckBox("2012");
    private JCheckBox YEAR2013=new JCheckBox("2013");
    
    public Years(Frame aFrame)
    {
      super(aFrame, "Years", true);
      setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
      setSize(3*aFrame.getWidth()/4, aFrame.getHeight());
      setResizable( false );
      JPanel p1=new JPanel();
      JPanel p2=new JPanel();
      this.setLayout(new BorderLayout());
      p1.setLayout( new GridLayout(4, 3) );
      p2.setLayout(new FlowLayout());
      add(p1,BorderLayout.CENTER);
      add(p2,BorderLayout.SOUTH);
          
    
      if (! years[0].equals("")) YEAR2002.setSelected(true);
      if (! years[1].equals("")) YEAR2003.setSelected(true);
      if (! years[2].equals("")) YEAR2004.setSelected(true);
      if (! years[3].equals("")) YEAR2005.setSelected(true);
      if (! years[4].equals("")) YEAR2006.setSelected(true);
      if (! years[5].equals("")) YEAR2007.setSelected(true);
      if (! years[6].equals("")) YEAR2008.setSelected(true);
      if (! years[7].equals("")) YEAR2009.setSelected(true);
      if (! years[8].equals("")) YEAR2010.setSelected(true);
      if (! years[9].equals("")) YEAR2011.setSelected(true);
      if (! years[10].equals("")) YEAR2012.setSelected(true);
      if (! years[11].equals("")) YEAR2013.setSelected(true);
      
      p1.add(YEAR2002);
      p1.add(YEAR2003);
      p1.add(YEAR2004);
      p1.add(YEAR2005);
      p1.add(YEAR2006);
      p1.add(YEAR2007);
      p1.add(YEAR2008);
      p1.add(YEAR2009);
      p1.add(YEAR2010);
      p1.add(YEAR2011);
      p1.add(YEAR2012);
      p1.add(YEAR2013);
      YEAR2002.addItemListener(this);
      YEAR2003.addItemListener(this);
      YEAR2004.addItemListener(this);
      YEAR2005.addItemListener(this);
      YEAR2006.addItemListener(this);
      YEAR2007.addItemListener(this);
      YEAR2008.addItemListener(this);
      YEAR2009.addItemListener(this);
      YEAR2010.addItemListener(this);
      YEAR2011.addItemListener(this);
      YEAR2012.addItemListener(this);
      YEAR2013.addItemListener(this);

      
      
      JButton OK_but= new JButton( "OK" );
      p2.add( OK_but );
      
      setLocationRelativeTo( aFrame );
      
      OK_but.addActionListener(new ActionListener() {

        @Override
            public void actionPerformed(ActionEvent e) {                                                                                   
            dispose();
              }
          });    
    
      setVisible( true );
    }

    
        @Override
        public void itemStateChanged(ItemEvent e) {
            Object source= e.getItemSelectable();
            if( source == YEAR2002 ) {
                if (YEAR2002.getSelectedObjects()!=null) years[0]="2002.html";
                else years[0]="";
            }
           if( source == YEAR2003 ) {
                if (YEAR2003.getSelectedObjects()!=null) years[1]="2003.html";
                else years[1]="";
            }
           if( source == YEAR2004 ) {
                if (YEAR2004.getSelectedObjects()!=null) years[2]="2004.html";
                else years[2]="";
            }
           if( source == YEAR2005 ) {
                if (YEAR2005.getSelectedObjects()!=null) years[3]="2005.html";
                else years[3]="";
            }
           if( source == YEAR2006 ) {
                if (YEAR2006.getSelectedObjects()!=null) years[4]="2006.html";
                else years[4]="";
            }
           if( source == YEAR2007 ) {
                if (YEAR2007.getSelectedObjects()!=null) years[5]="2007.html";
                else years[5]="";
            }
           if( source == YEAR2008 ) {
                if (YEAR2008.getSelectedObjects()!=null) years[6]="2008.html";
                else years[6]="";
            }
           if( source == YEAR2009 ) {
                if (YEAR2009.getSelectedObjects()!=null) years[7]="2009.html";
                else years[7]="";
            }
           if( source == YEAR2010 ) {
                if (YEAR2010.getSelectedObjects()!=null) years[8]="2010.html";
                else years[8]="";
            }
           if( source == YEAR2011 ) {
                if (YEAR2011.getSelectedObjects()!=null) years[9]="2011.html";
                else years[9]="";
            }
           if( source == YEAR2012 ) {
                if (YEAR2012.getSelectedObjects()!=null) years[10]="2012.html";
                else years[10]="";
            }
           if( source == YEAR2013 ) {
                if (YEAR2013.getSelectedObjects()!=null) years[11]="2013.html";
                else years[11]="";
            }
        }
        
  }
  
      
  private class KritiriaEmfanisis extends JDialog {
            
      JComboBox appearancesBox=new JComboBox(APPS);                 
      JComboBox topselectionsBox=new JComboBox(TOPSEL);                 
      JTextField exclusions=new JTextField(30);
      
      public KritiriaEmfanisis(Frame aFrame)  
      {
                
          super(aFrame, "Options", true);
          setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
          setResizable( false );     
          
          exclusions.setText(excl);
          appearancesBox.setSelectedIndex(appearance/2);          
          for (int i=0; i<TOPVAL.length; i++) {           
              if (top==TOPVAL[i]) {                          
                  topselectionsBox.setSelectedIndex(i);                         
                  break;                     
              }              
          }      
          JPanel p1=new JPanel();
          JPanel p2=new JPanel();
          this.setLayout(new BorderLayout());
          p1.setLayout( new GridLayout(3, 2) );
          p2.setLayout(new FlowLayout());
          add(p1,BorderLayout.CENTER);
          add(p2,BorderLayout.SOUTH);
          
          p1.add(new JLabel("Exclusions"));
          p1.add(exclusions);
          p1.add(new JLabel("Minimum Appearances"));
          p1.add(appearancesBox);
          p1.add(new JLabel("Tops"));
          p1.add(topselectionsBox);
              //Creating some components
              
          JButton OK_but= new JButton( "OK" );
          p2.add( OK_but );
          OK_but.addActionListener(new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {
                  excl=exclusions.getText();
                  appearance = 2*appearancesBox.getSelectedIndex();
                  top=TOPVAL[topselectionsBox.getSelectedIndex()];                                                                                   
                  dispose();
              }
          });
           
          setLocationRelativeTo( aFrame );

          pack();
              setVisible( true );
        }      
  }    

}