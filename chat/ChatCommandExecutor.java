package chat;

import Connections.Connection;

public interface ChatCommandExecutor {
	void execute(String[] parameters, Connection source);
}