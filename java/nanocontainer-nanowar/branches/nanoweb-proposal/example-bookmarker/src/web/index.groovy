import org.nanocontainer.nanoweb.example.bookmarker.spi.*;

class Action {
	
	def dao;
	
	@Property items;
	
	Action(BookmarkDAO _dao) {
		dao = _dao;
	}
	
	String execute() {
		items = dao.findAll();
		return "index.vm";
	}
}