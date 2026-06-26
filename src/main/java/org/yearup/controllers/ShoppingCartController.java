package org.yearup.controllers;

import org.yearup.models.ShoppingCart;
import org.yearup.models.User;
import org.yearup.service.ShoppingCartService;
import org.yearup.service.UserService;
import org.yearup.models.ShoppingCartItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")

public class ShoppingCartController
{
    // a shopping cart controller depends on the service layer
    private ShoppingCartService shoppingCartService;
    private UserService userService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService)
    {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
    }



    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        // get the currently logged in username
        String userName = principal.getName();
        // find database user by username
        User user = userService.getByUserName(userName);

        // use the shoppingCartService to get all items in the cart and return the cart
        return shoppingCartService.getByUserId(user.getId());
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15  (15 is the productId to be added)
    @PostMapping("/products/{productId}")
    public ResponseEntity<ShoppingCart> addProduct(@PathVariable int productId, Principal principal)
    {
        User user = userService.getByUserName(principal.getName());
        ShoppingCart cart = shoppingCartService.addProduct(user.getId(), productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
        // return the updated cart with status 201 Created
    }



    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15  (15 is the productId to be updated)
    @PutMapping("/products/{productId}")
    public ShoppingCart updateProduct(@PathVariable int productId,
                                      @RequestBody ShoppingCartItem item,
                                      Principal principal)
    {
        User user = userService.getByUserName(principal.getName());
        return shoppingCartService.updateProduct(user.getId(), productId, item.getQuantity());
    }
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated; return the cart (200 OK)


    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart  - return the (now empty) cart so the front end can refresh it (200 OK)
    @DeleteMapping
    public ShoppingCart clearCart(Principal principal)
    {
        User user = userService.getByUserName(principal.getName());
        return shoppingCartService.clearCart(user.getId());
    }
}
