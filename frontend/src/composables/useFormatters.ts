export function useFormatters() {
    const formatDate = (dateString: string) => {
        const date = new Date(dateString)
        return date.toLocaleDateString('es-ES', {
            day: '2-digit',
            month: 'long',
            year: 'numeric'
        })
    }

    const formatDateShort = (dateString: string) => {
        const date = new Date(dateString)
        return date.toLocaleDateString('es-ES', {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        })
    }

    const truncateText = (text: string, maxLength: number) => {
        if (!text || text.length <= maxLength) return text
        return text.substring(0, maxLength) + '...'
    }

    const formatCoordinates = (lat: number, lng: number, decimals = 4) => {
        return `${lat.toFixed(decimals)}, ${lng.toFixed(decimals)}`
    }

    return {
        formatDate,
        formatDateShort,
        truncateText,
        formatCoordinates
    }
}