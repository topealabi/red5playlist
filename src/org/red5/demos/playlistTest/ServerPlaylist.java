package org.red5.demos.playlistTest;

import java.util.Timer;
import java.util.TimerTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.red5.server.api.IScope;

import org.springframework.core.io.Resource;

import org.red5.server.api.stream.IServerStream;
import org.red5.server.api.stream.support.SimplePlayItem;
import org.red5.server.api.stream.support.StreamUtils;




public class ServerPlaylist implements IServerPlaylist  {
	
	protected static Logger log = LoggerFactory.getLogger(ServerPlaylist.class.getName());
	private String path = "";
	private String pattern = "";
	private String name = "";
	private Boolean repeat = true;
	private Boolean runOnStartUp = false;
	
	private IScope appScope;
	private IServerStream serverStream;
	
	public void ServerPlaylist()
	{
		
	}
	
	private String formatDate(Date date) {
		SimpleDateFormat formatter;
		String pattern = "dd/MM/yy H:mm:ss";
		Locale locale = new Locale("en", "US");
		formatter = new SimpleDateFormat(pattern, locale);
		return formatter.format(date);
	}
	
	public void setScope(IScope scope)
	{
		appScope = scope;
	}
	
	public void setRepeat(Boolean repeat)
	{
		this.repeat = repeat;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}
	
	public void setStreamName(String name)
	{
		this.name = name;
	}
	
	public void setRunOnStart(Boolean value)
	{
		runOnStartUp = value;
	}
	
	public Boolean getRunOnStart()
	{
		return runOnStartUp;
	}
	
	public void init(IScope scope)
	{
		appScope = scope;
		if (runOnStartUp)
		{
			start();
		}
	}
	
	public void init()
	{
		if (runOnStartUp)
		{
			start();
		}
	}
	
	public void shutdown()
	{
		stop();
	}
	
	public void start()
	{
		

	
			serverStream = StreamUtils.createServerStream(appScope, this.name);
			
			
			Map map = getDirectoryFileList(this.path, this.pattern);
			
			if (map.size() > 0)
			{
				Iterator it = map.keySet().iterator();
			
				while (it.hasNext()) {
				    String key = (String) it.next();
				    HashMap<String, Object> value = (HashMap<String, Object>) map.get(key);
				    //addItem(key);
				    SimplePlayItem item = new SimplePlayItem();
				    item.setName(key);
				    
					serverStream.addItem(item);
				    
				    log.debug("Adding " + value.get("name") + " to server playlist in order");
				}
				serverStream.setRewind(this.repeat);
				serverStream.start();
			} else {
				log.info("Nothing to add to playlist");
			}
	}
	
	public void stop()
	{
		serverStream.close();
	}
	
	public void addItem(String name, Boolean switchStream)
	{
		log.debug("Adding ITEM " + name);
		SimplePlayItem item = new SimplePlayItem();
	    item.setName(name);
		serverStream.addItem(item, switchStream ? 0 : null);
		serverStream.setItem(serverStream.getCurrentItemIndex());
	}
	
	public void addItem(String name)
	{
		SimplePlayItem item = new SimplePlayItem();
	    item.setName(name);
		serverStream.addItem(item);
	}
	
	public Map getDirectoryFileList(String path, String pattern) {
		//IScope scope = Red5.getConnectionLocal().getScope();
		Map<String, Map> filesMap = new HashMap<String, Map>();
		Map<String, Object> fileInfo;
		try {
			log.debug("getting the FLV files");
			Resource[] flvs = appScope.getResources(path + pattern);
			if (flvs != null) {
				for (Resource flv : flvs) {
					File file = flv.getFile();
					Date lastModifiedDate = new Date(file.lastModified());
					String lastModified = formatDate(lastModifiedDate);
					String flvName = flv.getFile().getName();
					String flvBytes = Long.toString(file.length());
					if (log.isDebugEnabled()) {
						log.debug("flvName: " + flvName);
						log.debug("lastModified date: " + lastModified);
						log.debug("flvBytes: " + flvBytes);
						log.debug("-------");
					}
					fileInfo = new HashMap<String, Object>();
					fileInfo.put("name", flvName);
					fileInfo.put("lastModified", lastModified);
					fileInfo.put("size", flvBytes);
					filesMap.put(flvName, fileInfo);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return filesMap;
	}
}
