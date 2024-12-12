import {BASE_URL} from "@/configs/config.js";
import {getCurrentUser} from "@/services/userService.js";

const API = `${BASE_URL}/orders`;

const placeOrder = async (orderData) => {

    const defaultTradingStockId = getCurrentUser().defaultTradingStock.id;

    const order = {
        price: orderData.price,
        quantity: orderData.quantity,
        userId: getCurrentUser().id,
    };

    if (orderData.orderType === 'Buy') {
        order.boughtStockId = orderData.stockId;
        order.soldStockId = defaultTradingStockId;
        order.orderType = "BUY"
    } else if (orderData.orderType === 'Sell') {
        order.soldStockId = orderData.stockId;
        order.boughtStockId = defaultTradingStockId;
        order.orderType = "SELL"
    } else {
        throw new Error("Unsupported operation");
    }

    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'cors': 'no-cors',
        },
        body: JSON.stringify(order)
    };

    try {
        const response = await fetch(API, options);
        if (!response.ok) {
            console.error(response.statusText);
            throw new Error(response.statusText);
        }
        const data = await response.json();
        console.log('Order placed successfully:', data);
        return data;
    } catch (error) {
        console.error('Error placing order:', error);
        throw new Error('Error placing order');
    }
};

export {
    placeOrder,
}