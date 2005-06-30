import org.nanocontainer.nanowar.nanoweb.result.*;
import org.nanocontainer.nanowar.nanoweb.example.bookmarker.spi.*;

class Action {
	
	def dao;

	@Property Bookmark item;

	Action(BookmarkDAO _dao) {
		dao = _dao;
	}

	String execute() {
		return edit();
	}

	String edit() {
		return "form.vm";
	}
	
	Redirect save() {
		dao.save(item);
		return new Redirect("index.nwa");
	}

	Redirect remove() {
		dao.delete(item);
		return new Redirect("index.nwa");
	}
	
}