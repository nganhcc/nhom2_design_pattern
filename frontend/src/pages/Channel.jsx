import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { get, postNoBody } from '../api'
import VideoCard from '../components/VideoCard'

export default function Channel() {
  const { id } = useParams()
  const [channel, setChannel] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    get(`/channels/${id}`)
      .then(setChannel)
      .catch((err) => setError(err.message || 'Failed to load channel'))
  }, [id])

  const toggleSubscription = async () => {
    setError('')
    try {
      const endpoint = channel?.subscribed ? `/channels/${id}/unsubscribe` : `/channels/${id}/subscribe`
      const result = await postNoBody(endpoint)
      if (result && result.subscriberCount != null) {
        setChannel((prev) => ({
          ...prev,
          subscribed: result.subscribed,
          subscriberCount: result.subscriberCount,
        }))
      }
    } catch (err) {
      setError(err.message || 'Subscription action failed')
    }
  }

  if (error) return <div className="card">Error: {error}</div>
  if (!channel) return <div>Loading...</div>

  return (
    <div>
      <h1 className="section-title">{channel.channelInfo.username}</h1>
      <div className="card">
        <div>{channel.channelInfo.email || 'No email'}</div>
        <div>Subscribers: {channel.subscriberCount}</div>
        <section className="pattern-panel">
          <h2>Proxy Pattern</h2>
          <p>
            Kênh được thao tác qua Proxy service backend: subscribe / unsubscribe đều là điều kiện truy cập ẩn.
            Backend quyết định cho phép hành động và trả về trạng thái đăng ký.
          </p>
          <button className="button" onClick={toggleSubscription}>
            {channel.subscribed ? 'Unsubscribe' : 'Subscribe'}
          </button>
        </section>
      </div>
      <h2 className="section-title">Videos</h2>
      <div className="grid grid-cols-3">
        {channel.videos.map((video) => (
          <VideoCard key={video.id} video={video} />
        ))}
      </div>
    </div>
  )
}
