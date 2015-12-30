package org.springfield.lou.application.session;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.session.ISession;
import org.springfield.lou.session.SessionContext;


public class PublicationbuilderSessionContext extends SessionContext {
	
	public PublicationbuilderSessionContext(Html5Application app) {
		super(app);
		// TODO Auto-generated constructor stub
	}

	public ISession createSession(Screen s) {
		System.out.println("publicationbuilder context createSession()");
		// TODO Auto-generated method stub
		ISession session = null;
		try{
			session = new PublicationbuilderSession(s, this.getApp());
			this.getSessions().add(session);
		}catch(Exception e){
			e.printStackTrace();
		}
		return session;
	}
	
}
