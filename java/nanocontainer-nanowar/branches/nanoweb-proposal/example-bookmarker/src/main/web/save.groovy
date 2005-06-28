import org.nanocontainer.nanoweb.example.bookmarker.spi.*;

class Action {
	
	@Property Bookmark item;

	String execute() {
		return "form.vm";
	}
}