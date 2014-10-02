package Install;

public class Start {

        /**
         * @param args
         */
        public static void main(String[] args) {
                
                boolean confOnly = false;
                boolean noConf = false;
                boolean start = true;
                boolean fset = false;
                String file = new String("server.xml");
                for (int i=0; i< args.length; i++){
                        if (args[i].contentEquals("-c") || args[i].contentEquals("--conf-only")){
                                confOnly = true;
                        }
                        else if (args[i].contentEquals("-f") || args[i].contentEquals("--file")){
                                file = args[i+1];
                                i++;
                                fset = true;
                        }
                        else if (args[i].contentEquals("-n") || args[i].contentEquals("--no-conf")){
                                noConf = true;
                        }
                        else if (args[i].contentEquals("-h") || args[i].contentEquals("--help")){
                                usage();
                                start = false;
                        }
                        else {
                                System.out.println("option " + args[i] + " not supported, exiting");
                                start = false;
                                usage();
                                break;
                        }
                }
                
                if (noConf && start) System.out.println("Configuration file will not be altered");
                if (confOnly && start) System.out.println("Database will not be altered");
                if (fset && start) System.out.println("Configuration file will be save as " + file);
                if (noConf && confOnly){ System.out.println("Nothing to do, exiting"); usage(); return; }
                if (start) new Installer(confOnly, noConf, file);

        }
        
        public static void usage(){
                System.out.println("OpenHeroes installer");
                System.out.println("usage: java -jar Install-bin.jar <options>");
                System.out.println("Available options:");
                System.out.println("-c, --conf-only       Only generate configuration file");
                System.out.println("-f, --file [filename] Specify filename for configuration file");
                System.out.println("-n, --no-conf         No configuration file will be generated");
                System.out.println("-h, --help            Show help");
                System.out.println("");
        }

}