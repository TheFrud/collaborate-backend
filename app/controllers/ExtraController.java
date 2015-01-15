package controllers;

import play.Logger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;


public class ExtraController extends Action.Simple {
	
	@Override
	public Promise<Result> call(Context ctx) throws Throwable {
		Logger.info("Calling action for " + ctx);
		return delegate.call(ctx);
	}
}
