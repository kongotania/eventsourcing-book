import {v4} from "uuid";
import {CommandResult} from "@/app/components/types/CommandResult";

export const changeprice = async (productId: string, price:number, oldPrice: number):Promise<CommandResult> => {
    let response = await fetch(`/debug/external/changeprice?productId=${productId}&price=${price}&oldPrice=${oldPrice}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    return response.json()
}