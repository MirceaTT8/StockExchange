package com.javazerozahar.stock_exchange;

import com.javazerozahar.stock_exchange.model.entity.Portfolio;
import com.javazerozahar.stock_exchange.model.entity.Stock;
import com.javazerozahar.stock_exchange.model.entity.StockHistory;
import com.javazerozahar.stock_exchange.model.entity.User;
import com.javazerozahar.stock_exchange.repository.*;
import com.javazerozahar.stock_exchange.utils.PasswordHasher;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Configuration
@AllArgsConstructor
public class Initializer {

    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final TransactionRepository transactionRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;
    private final PasswordHasher passwordHasher;

    public void initialize(boolean testing) {

        User user1 = User.builder()
                .lastName("Doe")
                .firstName("John")
                .email("johndoe@example.com")
                .password(passwordHasher.getHash("abc"))
                .phoneNumber("+40712345678")
                .build();

        User user2 = User.builder()
                .lastName("Smith")
                .firstName("Jane")
                .email("janesmith@example.com")
                .password(passwordHasher.getHash("def"))
                .phoneNumber("+40720456789")
                .build();

        User user3 = User.builder()
                .lastName("Johnson")
                .firstName("Alice")
                .email("alicejohnson@example.com")
                .password(passwordHasher.getHash("ghi"))
                .phoneNumber("+40730123456")
                .build();

        List<User> users = List.of(user1, user2, user3);
        userRepository.saveAll(users);

        // Adding some Stock entries
        Stock stock1 = Stock.builder().id(1L).symbol("AAPL").name("Apple Inc.").price(150.0).isActive(true).build();
        Stock stock2 = Stock.builder().id(2L).symbol("GOOGL").name("Alphabet Inc.").price(2800.0).isActive(true).build();
        Stock stock3 = Stock.builder().id(3L).symbol("$EUR").name("Euro").price(1.0).isActive(true).build();
        Stock stock4 = Stock.builder().id(4L).symbol("TSLA").name("Tesla, Inc.").price(255.0).isActive(true).build();
        Stock stock5 = Stock.builder().id(5L).symbol("NVDA").name("Nvidia Corporation").price(137.0).isActive(true).build();
        Stock stock6 = Stock.builder().id(6L).symbol("META").name("Meta Platforms, Inc.").price(570.0).isActive(true).build();

        stockRepository.save(stock1);
        stockRepository.save(stock2);
        stockRepository.save(stock3);
        stockRepository.save(stock4);
        stockRepository.save(stock5);
        stockRepository.save(stock6);

        // Fetch and display all stocks
        List<Stock> allStocks = stockRepository.findAll().stream().filter(stock -> stock.getId() != 3).toList();
        //System.out.println("Stocks:");
        //allStocks.forEach(System.out::println);

        // Adding Stock history for a stock

        for (Stock stock : allStocks) {
            int startPrice = (int) (Math.random() * 296) + 5;
            stockHistoryRepository.saveAll(generateStockHistory(stock, startPrice, 2, 1000, 1000));
            stockHistoryRepository.saveAll(generateStockHistory(stock, startPrice, 0.3, 40, 5));
        }


        allStocks = stockRepository.findAll();

        for (Stock stock : allStocks) {
            long veryEarlyTimestamp = 0;

            List<StockHistory> histories = stockHistoryRepository.findByStockIdAndTimestampAfter(stock.getId(), veryEarlyTimestamp);

            StockHistory latestHistory = histories.stream()
                    .max(Comparator.comparingLong(StockHistory::getTimestamp))
                    .orElse(null);

            if (latestHistory != null && !testing) {
                stock.setPrice(latestHistory.getPrice());

                stockRepository.save(stock);
            }
        }

        // Fetch and display stock history for stock1
        List<StockHistory> stock1History = stockHistoryRepository.findByStockId(stock1.getId());
        //System.out.println("Stock History for AAPL:");

        List<Portfolio> portfolios = List.of(
                Portfolio.builder()
                        .user(user1)
                        .quantity(0.0)
                        .stock(stock1)
                        .build(),
                Portfolio.builder()
                        .user(user2)
                        .quantity(100.0)
                        .stock(stock1)
                        .build(),
                Portfolio.builder()
                        .user(user1)
                        .quantity(10000.0)
                        .stock(stock3)
                        .build(),
                Portfolio.builder()
                        .user(user2)
                        .quantity(10000.0)
                        .stock(stock3)
                        .build()
        );

        portfolioRepository.saveAll(portfolios);

        portfolioRepository.saveAll(portfolios);
//
//            List<Account> allAccounts = accountRepo.findAll();
//            System.out.println("Accounts:");
//            allAccounts.forEach(System.out::println);
//
//            Transaction transaction1 = Transaction.builder()
//                    .stockId(stock1.getId())
//                    .sellerId(account1.getUserId())
//                    .buyerId(account2.getUserId())
//                    .quantity(5)
//                    .price(150.0)
//                    .timestamp(System.currentTimeMillis())
//                    .build();
//
//            Transaction transaction2 = Transaction.builder()
//                    .stockId(stock2.getId())
//                    .sellerId(account2.getUserId())
//                    .buyerId(account1.getUserId())
//                    .quantity(3)
//                    .price(2800.0)
//                    .timestamp(System.currentTimeMillis())
//                    .build();

//            transactionRepo.save(transaction1);
//            transactionRepo.save(transaction2);

//            System.out.println("Transactions for AAPL:");
//            List<Transaction> transactionsForAAPL = transactionRepo.findAllByStockId(stock1.getId());
//            transactionsForAAPL.forEach(System.out::println);
//
//            System.out.println("Transactions involving User with ID 1 (Account 1):");
//            List<Transaction> transactionsByUser1 = transactionRepo.findAllByUserId(account1.getUserId());
//            transactionsByUser1.forEach(System.out::println);
//
//            Optional<Stock> stockToUpdate = stockRepo.findById(stock1.getId());
//            if (stockToUpdate.isPresent()) {
//                Stock stock = stockToUpdate.get();
//                stock.setPrice(155.0);
//                stockRepo.update(stock);
//                System.out.println("Updated Stock: " + stockRepo.findById(stock.getId()).get());
//            }

        // Update account balance after a transaction
//            account1.setBalance(account1.getBalance() - transaction1.getPrice() * transaction1.getQuantity()); // Deduct buyer's cost
//            accountRepo.update(account1);
//
//            account2.setBalance(account2.getBalance() + transaction1.getPrice() * transaction1.getQuantity()); // Add seller's profit
//            accountRepo.update(account2);
//
//            account2.setBalance(account2.getBalance() + transaction2.getPrice() * transaction2.getQuantity()); // Deduct buyer's cost
//            accountRepo.update(account2);
//
//            account1.setBalance(account1.getBalance() - transaction2.getPrice() * transaction2.getQuantity()); // Add seller's profit
//            accountRepo.update(account1);
//
//            // Display updated account balances
//            System.out.println("Updated Account Balances:");
//            System.out.println("Account 1 (User 1): " + account1.getBalance());
//            System.out.println("Account 2 (User 2): " + account2.getBalance());
//
//            // Delete a transaction and display remaining transactions
//            transactionRepo.deleteById(transaction1.getId());
//            System.out.println("Transactions after deletion:");
//            List<Transaction> allTransactions = transactionRepo.findAll();
//            allTransactions.forEach(System.out::println);
    }

    public void reset() {
        portfolioRepository.deleteAll();
        transactionRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
        stockHistoryRepository.deleteAll();
        stockRepository.deleteAll();
    }

    private List<StockHistory> generateStockHistory(Stock stock, int basePrice, double fluctuation, int entries, int days) {
        List<StockHistory> stockHistories = new ArrayList<>();

        long currentTime = System.currentTimeMillis();
        long oneDayMillis = 24 * 60 * 60 * 1000;
        long totalSpanMillis = days * oneDayMillis;

        double minimumStockValue = 0.5;

        double fluctuationLimit = basePrice * fluctuation;
        long timeIncrement = totalSpanMillis / entries;

        for (int i = 0; i < entries; i++) {
            double priceFluctuation = (Math.random() * 2 * fluctuationLimit) - fluctuationLimit;
            StockHistory history = StockHistory.builder()
                    .stock(stock)
                    .price(Math.abs(basePrice + priceFluctuation) + minimumStockValue)
                    .timestamp(currentTime - (i * timeIncrement))
                    .build();
            stockHistories.add(history);
        }

        return stockHistories;
    }

    public void initialize() {
        initialize(false);
    }

}
