
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

public class Global extends GlobalSettings {

 // For CORS
 private class ActionWrapper extends Action.Simple {
 public ActionWrapper(Action<?> action) {
 this.delegate = action;
 }

 @Override
 public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
 Promise<Result> result = this.delegate.call(ctx);
 Http.Response response = ctx.response();
 response.setHeader("Access-Control-Allow-Origin", "*");
 return result;
 }
 }

 @Override
 public Action<?> onRequest(Http.Request request,
 java.lang.reflect.Method actionMethod) {
 return new ActionWrapper(super.onRequest(request, actionMethod));
 }
 
 @Override
 public void onStart(Application app) {
   Logger.info("Application has started Have fun!");
 }  
 
 @Override
 public void onStop(Application app) {
   Logger.info("Application shutdown...");
 } 
}