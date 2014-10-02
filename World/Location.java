package World;

import java.nio.channels.*;

// <<Interface>>location
// Interface for accessing all the objects within the in-game map stored in the grid system


public interface Location
{
  int getuid();
  void setuid(int uid);
  float getlastknownX();
  float getlastknownY();
  Waypoint getLocation();
  SocketChannel GetChannel();
  short getState();
  void updateEnvironment(Integer players, boolean add);
}