package Player.Dolls;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.code.chatterbotapi.*;

public class Cleverdoll extends Thread{
	
	private ChatterBotFactory factory;
	private ChatterBot bot;
	private ChatterBotSession botSession;
	private List<String> message=Collections.synchronizedList(new LinkedList<String>());
	private List<Doll> doll=Collections.synchronizedList(new LinkedList<Doll>());
	private static Cleverdoll instance=null;
	
	public Cleverdoll() throws Exception {
		
		factory = new ChatterBotFactory();
		bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
		botSession = bot.createSession();
		start();
		
	}
	
	public static Cleverdoll getInstance(){
		try{
			if(instance==null){
				instance=new Cleverdoll();
			}
			return instance;
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public void run(){
		while(true){
			think();
		}
	}

	public void think(){
		synchronized(message){
			synchronized(doll){
				while(!doll.isEmpty()){
					try{
						Doll d=doll.remove(0);
						String s=botSession.think(message.remove(0));
						d.writeInPublicChat(s);
						Thread.sleep(1000);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void thinkAbout(Doll doll, String s){
		synchronized(message){
			synchronized(this.doll){
				message.add(s);
				this.doll.add(doll);
			}
		}
	}

}
