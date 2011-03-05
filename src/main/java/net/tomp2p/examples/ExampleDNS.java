package net.tomp2p.examples;

import java.io.IOException;

import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

/**
 * See http://tomp2p.net/doc/quick/ for more information on this
 */
public class ExampleDNS
{
	final private Peer peer;
	public ExampleDNS(int nodeId) throws Exception {
		peer=new Peer(Number160.createHash(nodeId));
		peer.listen(4000+nodeId, 4000+nodeId);
		FutureBootstrap fb=this.peer.bootstrapBroadcast(4001);
		fb.awaitUninterruptibly();
		peer.discover(fb.getBootstrapTo().iterator().next()).awaitUninterruptibly();
		
	}
	public static void main(String[] args) throws NumberFormatException, Exception {
		ExampleDNS dns=new ExampleDNS(Integer.parseInt(args[0]));
		if(args.length==3) {
			dns.store(args[1],args[2]);
		}
		if(args.length==2) {
			System.out.println("Name:"+args[1]+" IP:"+dns.get(args[1]));
		}
	}
	private String get(String name) throws ClassNotFoundException, IOException
	{
		FutureDHT futureDHT=peer.get(Number160.createHash(name));
		futureDHT.awaitUninterruptibly();
		if(futureDHT.isSuccess()) {
			return futureDHT.getData().values().iterator().next().getObject().toString();
		}
		return "not found";
	}
	private void store(String name, String ip) throws IOException
	{
		peer.put(Number160.createHash(name), new Data(ip)).awaitUninterruptibly();
	}
}
