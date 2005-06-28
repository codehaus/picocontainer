import org.nanocontainer.nanoweb.example.bookmarker.spi.*;

class Action {

	def dao;

	@Property Bookmark item;

	Action(BookmarkDAO _dao) {
		dao = _dao;
	}

	String execute() {
		dao.delete(item);
		return "index.nwa";
	}

}