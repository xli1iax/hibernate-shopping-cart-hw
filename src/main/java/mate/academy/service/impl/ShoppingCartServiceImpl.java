package mate.academy.service.impl;

import java.util.ArrayList;
import java.util.Optional;
import mate.academy.dao.ShoppingCartDao;
import mate.academy.dao.TicketDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.MovieSession;
import mate.academy.model.ShoppingCart;
import mate.academy.model.Ticket;
import mate.academy.model.User;
import mate.academy.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Inject
    private ShoppingCartDao shoppingCartDao;

    @Inject
    private TicketDao ticketDao;

    @Override
    public void addSession(MovieSession movieSession, User user) {
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setMovieSession(movieSession);

        Optional<ShoppingCart> shoppingCartOptional = shoppingCartDao.getByUser(user);

        if (shoppingCartOptional.isEmpty()) {
            ShoppingCart newShoppingCart = new ShoppingCart();
            newShoppingCart.setUser(user);

            newShoppingCart.getTickets().add(ticket);
            ticket.setShoppingCart(newShoppingCart);

            shoppingCartDao.add(newShoppingCart);
        } else {
            ShoppingCart shoppingCart = shoppingCartOptional.get();

            shoppingCart.getTickets().add(ticket);
            ticket.setShoppingCart(shoppingCart);

            shoppingCartDao.update(shoppingCart);
        }
    }

    @Override
    public ShoppingCart getByUser(User user) {
        return shoppingCartDao.getByUser(user).orElseThrow(
                () -> new DataProcessingException("Can not get user for shoping cart: " + user));
    }

    @Override
    public void registerNewShoppingCart(User user) {
        Optional<ShoppingCart> shoppingCartOptional = shoppingCartDao.getByUser(user);
        if (shoppingCartOptional.isEmpty()) {
            ShoppingCart newShoppingCart = new ShoppingCart();
            newShoppingCart.setUser(user);
            newShoppingCart.setTickets(new ArrayList<>());
            shoppingCartDao.add(newShoppingCart);
        }
    }

    @Override
    public void clear(ShoppingCart shoppingCart) {
        shoppingCart.getTickets().clear();
        shoppingCartDao.update(shoppingCart);
    }
}
