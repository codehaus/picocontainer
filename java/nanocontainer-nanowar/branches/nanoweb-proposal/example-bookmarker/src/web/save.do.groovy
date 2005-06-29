import org.nanocontainer.nanowar.nanoweb.example.bookmarker.spi.*;

class Action {

	def dao;

	@Property Bookmark item;

	Action(BookmarkDAO _dao) {
		dao = _dao;
	}

	String execute() {
		dao.save(item);
		return "index.nwa";
	}

}