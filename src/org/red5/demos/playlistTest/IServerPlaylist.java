package org.red5.demos.playlistTest;

import org.red5.server.api.IScope;

public interface IServerPlaylist {

	public void start();
	public void stop();
	public void shutdown();
	public void init();
	public void init(IScope scope);
	
	public void setScope(IScope scope);
	public void setRepeat(Boolean repeat);
	public void setPath(String path);
	public void setPattern(String pattern);
	public void setStreamName(String name);
	public void setRunOnStart(Boolean value);
	public Boolean getRunOnStart();
}
